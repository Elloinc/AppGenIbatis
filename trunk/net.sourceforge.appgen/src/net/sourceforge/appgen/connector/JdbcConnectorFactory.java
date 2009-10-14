/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.appgen.connector;

import net.sourceforge.appgen.model.ConnectionInformation;

/**
 * @author Byeongkil Woo
 */
public class JdbcConnectorFactory {

	public static JdbcConnector createConnector(ConnectionInformation connectionInformation) {
		if (connectionInformation.getDatabaseType().equals(ConnectionInformation.DATABASE_TYPE_ORACLE)) {
			return new OracleConnector(connectionInformation);
		}
		
		if (connectionInformation.getDatabaseType().equals(ConnectionInformation.DATABASE_TYPE_MYSQL)) {
			return new MySqlConnector(connectionInformation);
		}
		
		throw new RuntimeException("Not suppoerted database type: " + connectionInformation.getDatabaseType());
	}
	
}
