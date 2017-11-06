/*
 * Copyright (C) 2017. OpenLattice, Inc
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can contact the owner of the copyright at support@openlattice.com
 *
 */

package com.openlattice.postgres;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author Matthew Tamayo-Rios &lt;matthew@openlattice.com&gt;
 */
public class PostgresArrays {

    public static Array createUuidArray( Connection connection, Stream<UUID> ids ) throws SQLException {
        return connection.createArrayOf( PostgresDatatype.UUID.sql(), ids.toArray( UUID[]::new ) );
    }

    public static Array createTextArray( Connection connection, Stream<String> ids ) throws SQLException {
        return connection.createArrayOf( PostgresDatatype.TEXT.sql(), ids.toArray( String[]::new ) );
    }

    public static String[] getTextArray( ResultSet rs, String column ) throws SQLException {
        return (String[]) rs.getArray( column ).getArray();
    }

    public static UUID[] getUuidArray( ResultSet rs, String column ) throws SQLException {
        return (UUID[]) rs.getArray( column ).getArray();
    }
}