/*******************************************************************************
 * Copyright (C) 2012 JMaNGOS <http://jmangos.org/>
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
/**
 * 
 */
package org.jmangos.commons.network.utils;

import java.nio.ByteOrder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * The Class NetworkUtil.
 * 
 * @author vangula
 */
public class NetworkUtil {

    /**
     * New channel buffer.
     * 
     * @return the channel buffer
     */
    public static final ChannelBuffer newChannelBuffer() {

        return newChannelBuffer(65536);
    }

    /**
     * New channel buffer.
     * 
     * @param size
     *        the size
     * @return the channel buffer
     */
    public static final ChannelBuffer newChannelBuffer(final int size) {

        return ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, size);
    }

    /**
     * New dinamic channel buffer.
     * 
     * @param size
     *        the size
     * @return the channel buffer
     */
    public static final ChannelBuffer newDinamicChannelBuffer(final int size) {

        return ChannelBuffers.dynamicBuffer(ByteOrder.LITTLE_ENDIAN, size);
    }
}
