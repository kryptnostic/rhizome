/*
 * Copyright (C) 2020. OpenLattice, Inc.
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
 *
 */

package com.geekbeast.rhizome.jobs

import com.openlattice.serializer.AbstractJacksonSerializationTest
import org.apache.commons.lang3.RandomStringUtils

/**
 *
 * @author Matthew Tamayo-Rios &lt;matthew@openlattice.com&gt;
 */
class JobSerializationTests : AbstractJacksonSerializationTest<DistributedJobState>() {
    override fun getSampleData(): DistributedJobState = DistributedJobState(0, JobStatus.RUNNING, EmptyJobState(RandomStringUtils.random(5)) )
    override fun logResult(result: SerializationResult<DistributedJobState>?) {
        logger.info("Json: ${result?.jsonString}")
    }

    override fun getClazz(): Class<DistributedJobState> = DistributedJobState::class.java
}
