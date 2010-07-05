/*
 * Copyright (c) 2010, Stanislav Muhametsin. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package org.qi4j.entitystore.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.qi4j.api.common.Visibility;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.entitystore.sql.bootstrap.PostgreSQLEntityStoreAssembler;
import org.qi4j.entitystore.sql.database.PostgreSQLConfiguration;
import org.qi4j.entitystore.sql.database.PostgreSQLDatabaseSQLServiceMixin;
import org.qi4j.library.sql.common.SQLUtil;
import org.qi4j.test.entity.AbstractEntityStoreTest;

/**
 *
 * @author Stanislav Muhametsin
 */
public class PostgreSQLEntityStoreTest extends AbstractEntityStoreTest
{

    @Override
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        super.assemble( module );
        new PostgreSQLEntityStoreAssembler().assemble( module );
        ModuleAssembly config = module.layerAssembly().moduleAssembly( "config" );
        config.addServices( MemoryEntityStoreService.class );
        config.addEntities( PostgreSQLConfiguration.class ).visibleIn( Visibility.layer );
    }

    @Override
    public void tearDown()
        throws Exception
    {

        UnitOfWork uow = this.unitOfWorkFactory.newUnitOfWork();
        try
        {
            PostgreSQLConfiguration config = uow.get( PostgreSQLConfiguration.class, PostgreSQLEntityStoreAssembler.SERVICE_NAME );
            Connection connection = DriverManager.getConnection( config.connectionString().get() );
            String schemaName = config.schemaName().get();
            if (schemaName == null)
            {
                schemaName = PostgreSQLDatabaseSQLServiceMixin.DEFAULT_SCHEMA_NAME;
            }

            Statement stmt = null;
            try
            {
                stmt = connection.createStatement();
                stmt.execute( String.format( "DELETE FROM %s." + PostgreSQLDatabaseSQLServiceMixin.TABLE_NAME, schemaName ));
                connection.commit();
            } finally
            {
                SQLUtil.closeQuietly( stmt );
            }
        } finally
        {
            uow.discard();
            super.tearDown();
        }
    }
}