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
package org.jmangos.realm.network.netty.packet.client;

import java.nio.BufferUnderflowException;

import javax.inject.Inject;
import javax.inject.Named;

import org.jmangos.commons.network.netty.sender.AbstractPacketSender;
import org.jmangos.realm.network.netty.packet.AbstractWoWClientPacket;
import org.jmangos.realm.network.netty.packet.server.SMSG_REALM_SPLIT;

// TODO: Auto-generated Javadoc
/**
 * The Class CMSG_REALM_SPLIT.
 */
public class CMSG_REALM_SPLIT extends AbstractWoWClientPacket {

	/** The sender. */
	@Inject
	@Named("client")
	private AbstractPacketSender sender;

	/** The unk. */
	int unk;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wowemu.common.network.model.ReceivablePacket#readImpl()
	 */
	@Override
	protected void readImpl() throws BufferUnderflowException, RuntimeException {
		unk = readD();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wowemu.common.network.model.ReceivablePacket#runImpl()
	 */
	@Override
	protected void runImpl() {
		sender.send(getClient(), new SMSG_REALM_SPLIT(unk));

	}
}