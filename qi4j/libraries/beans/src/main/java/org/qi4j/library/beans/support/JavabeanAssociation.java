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

import org.qi4j.api.common.QualifiedName;
import org.qi4j.api.association.Association;
import org.qi4j.api.composite.TransientBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import org.qi4j.api.association.AssociationDescriptor;

public class JavabeanAssociation
    implements Association
{
    private final JavabeanMixin javabeanMixin;
    private final AssociationDescriptor descriptor;
    public final Method pojoMethod;

    public JavabeanAssociation( JavabeanMixin javabeanMixin, AssociationDescriptor descriptor, Method pojoMethod )
    {
        this.javabeanMixin = javabeanMixin;
        this.descriptor = descriptor;
        this.pojoMethod = pojoMethod;
    }

    public <T> T metaInfo( Class<T> infoType )
    {
        return descriptor.metaInfo( infoType );
    }

    public QualifiedName qualifiedName()
    {
        return descriptor.qualifiedName();
    }

    public Type type()
    {
        return descriptor.type();
    }

    public boolean isImmutable()
    {
        return descriptor.isImmutable();
    }

    public boolean isAggregated()
    {
        return descriptor.isAggregated();
    }

    public Object get()
    {
        try
        {
            Object resultObject = pojoMethod.invoke( javabeanMixin.pojo );
            Class type = (Class) type();
            if( type.isInterface() )
            {
                TransientBuilder<?> builder = javabeanMixin.cbf.newTransientBuilder( type );
                builder.use( resultObject );
                return builder.newInstance();
            }
            return resultObject;
        }
        catch( IllegalAccessException e )
        {
            throw new IllegalArgumentException( "POJO is not compatible with JavaBeans specification. Method must be public: " + pojoMethod );
        }
        catch( InvocationTargetException e )
        {
            throw new UndeclaredThrowableException( e.getTargetException() );
        }
    }

    public void set( Object associated ) throws IllegalArgumentException
    {
        //TODO: Auto-generated, need attention.
    }
}
