/*
 * Copyright 2008 Niclas Hedhman. All rights Reserved.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package org.qi4j.library.beans.support;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.qi4j.api.Qi4j;
import org.qi4j.api.common.AppliesTo;
import org.qi4j.api.common.AppliesToFilter;
import org.qi4j.api.composite.Composite;
import org.qi4j.api.composite.TransientBuilderFactory;
import org.qi4j.api.association.Association;
import org.qi4j.api.association.AssociationDescriptor;
import org.qi4j.api.association.AssociationStateDescriptor;
import org.qi4j.api.association.ManyAssociation;
import org.qi4j.api.composite.CompositeDescriptor;
import org.qi4j.api.composite.StateDescriptor;
import org.qi4j.api.composite.StatefulCompositeDescriptor;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.injection.scope.Uses;
import org.qi4j.api.property.Property;
import org.qi4j.api.property.PropertyDescriptor;
import org.qi4j.api.structure.Module;

@AppliesTo( { JavabeanMixin.JavabeanSupportFilter.class } )
public class JavabeanMixin
    implements JavabeanSupport, InvocationHandler
{
    private HashMap<AccessibleObject, Object> handlers;

    @Structure TransientBuilderFactory cbf;
    Object pojo;

    public JavabeanMixin( @Structure Module module, @This Composite thisComposite, @Uses Object pojo )
    {
        this.pojo = pojo;
        this.handlers = new HashMap<AccessibleObject, Object>();
        CompositeDescriptor thisDescriptor = Qi4j.FUNCTION_DESCRIPTOR_FOR.map( thisComposite );
        if( thisDescriptor instanceof StatefulCompositeDescriptor )
        {
            StateDescriptor stateDescriptor = ( (StatefulCompositeDescriptor) thisDescriptor ).state();
            for( PropertyDescriptor propDesc : stateDescriptor.properties() )
            {
                Method pojoMethod = findMethod( pojo, propDesc.qualifiedName().name() );
                handlers.put( propDesc.accessor(), new JavabeanProperty( this, propDesc, pojoMethod ) );
            }
            if( stateDescriptor instanceof AssociationStateDescriptor )
            {
                AssociationStateDescriptor assocStateDesc = (AssociationStateDescriptor) stateDescriptor;
                for( AssociationDescriptor assocDesc : assocStateDesc.associations() )
                {
                    Method pojoMethod = findMethod( pojo, assocDesc.qualifiedName().name() );
                    handlers.put( assocDesc.accessor(), new JavabeanAssociation( this, assocDesc, pojoMethod ) );
                }
                for( AssociationDescriptor assocDesc : assocStateDesc.manyAssociations() )
                {
                    Method pojoMethod = findMethod( pojo, assocDesc.qualifiedName().name() );
                    handlers.put( assocDesc.accessor(), new JavabeanManyAssociation( this, assocDesc, pojoMethod ) );
                }
            }
        }
    }

    private Method findMethod( Object pojo, String name )
    {
        String methodName = "get" + Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 );
        Method pojoMethod;
        try
        {
            pojoMethod = pojo.getClass().getMethod( methodName );
        }
        catch( NoSuchMethodException e )
        {
            methodName = "is" + Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 );
            try
            {
                pojoMethod = pojo.getClass().getMethod( methodName );
            }
            catch( NoSuchMethodException e1 )
            {
                throw new IllegalArgumentException( methodName + " is not present in " + pojo.getClass() );
            }
        }
        return pojoMethod;
    }

    public Object getJavabean()
    {
        return pojo;
    }

    public void setJavabean( Object data )
    {
        pojo = data;
    }

    public Object invoke( Object proxy, Method method, Object[] args )
        throws Throwable
    {
        synchronized( this )
        {
            return handlers.get( method );
        }
    }

    public static class JavabeanSupportFilter
        implements AppliesToFilter
    {
        public boolean appliesTo( Method method, Class<?> mixin, Class<?> compositeType, Class<?> modifierClass )
        {
            String methodName = method.getName();
            Class<?> retType = method.getReturnType();
            return Property.class.isAssignableFrom( retType ) ||
                   Association.class.isAssignableFrom( retType ) ||
                   ManyAssociation.class.isAssignableFrom( retType ) ||
                   "getJavabean".equals( methodName ) || "setJavabean".equals( methodName );
        }
    }

}
