package org.qi4j.entitystore.rmi;

import org.qi4j.api.common.AppliesTo;
import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.service.ServiceActivation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Create and delegate to a RMI registry.
 */
@AppliesTo( { Registry.class, ServiceActivation.class } )
public class RegistryMixin
    implements InvocationHandler, ServiceActivation
{
    Registry registry;

    @This Configuration<RegistryConfiguration> config;

    public void activateService() throws Exception
    {
        try
        {
            Integer port = config.get().port().get();
            registry = LocateRegistry.createRegistry( port );
        }
        catch( RemoteException e )
        {
            registry = LocateRegistry.getRegistry();
        }
    }

    public void passivateService() throws Exception
    {
        for( String boundService : registry.list() )
        {
            registry.unbind( boundService );
        }
        UnicastRemoteObject.unexportObject( registry, true );
    }

    public Object invoke( Object o, Method method, Object[] objects ) throws Throwable
    {
        return method.invoke( registry, objects );
    }

}
