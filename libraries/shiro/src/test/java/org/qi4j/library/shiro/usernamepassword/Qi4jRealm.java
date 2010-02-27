/*
 * Copyright (c) 2010 Paul Merlin <paul@nosphere.org>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.qi4j.library.shiro.usernamepassword;

import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.QueryBuilderFactory;
import static org.qi4j.api.query.QueryExpressions.*;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.library.shiro.domain.RoleAssignee;
import org.qi4j.library.shiro.domain.SecureHashSecurable;
import org.qi4j.library.shiro.realms.AbstractSecureHashQi4jRealm;

/**
 * @author Paul Merlin <paul@nosphere.org>
 */
public class Qi4jRealm
        extends AbstractSecureHashQi4jRealm
{

    @Structure
    private QueryBuilderFactory qbf;

    public Qi4jRealm()
    {
        super();
        setName( Qi4jRealm.class.getSimpleName() );
    }

    @Override
    protected SecureHashSecurable getSecureHashSecurable( String username )
    {
        return findUserEntityByUsername( username );
    }

    @Override
    protected RoleAssignee getRoleAssignee( String username )
    {
        return findUserEntityByUsername( username );
    }

    private UserEntity findUserEntityByUsername( String username )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        QueryBuilder<UserEntity> queryBuilder = qbf.newQueryBuilder( UserEntity.class );
        UserEntity userTemplate = templateFor( UserEntity.class );
        queryBuilder = queryBuilder.where( eq( userTemplate.username(), username ) );
        Query<UserEntity> query = queryBuilder.newQuery( uow ).maxResults( 1 );
        return query.iterator().next();
    }

}
