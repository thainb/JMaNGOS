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
package org.jmangos.realm.network.packet.wow.server;

import java.util.List;

import org.jmangos.commons.enums.EquipmentSlots;
import org.jmangos.commons.service.ServiceContent;
import org.jmangos.realm.entities.CharacterData;
import org.jmangos.realm.entities.FieldsItem;
import org.jmangos.realm.network.packet.wow.AbstractWoWServerPacket;
import org.jmangos.realm.network.packet.wow.client.CMSG_AUTH_SESSION;
import org.jmangos.realm.service.ItemStorages;
import org.jmangos.world.entities.ItemPrototype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SMSG_CHAR_ENUM.
 */
public class SMSG_CHAR_ENUM extends AbstractWoWServerPacket {

    /** The charlist. */
    private final List<CharacterData> charlist;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CMSG_AUTH_SESSION.class);

    /**
     * Instantiates a new <tt>SMSG_CHAR_ENUM</tt> packet.
     * 
     * @param charlist
     *        the charlist
     */
    public SMSG_CHAR_ENUM(final List<CharacterData> charlist) {

        this.charlist = charlist;
    }

    /**
     * @see org.jmangos.commons.network.model.SendablePacket#writeImpl()
     */
    @Override
    public void writeImpl() {

        logger.info("CHARLIST SIZE " + this.charlist.size());
        writeC(this.charlist.size());
        for (final CharacterData characterData : this.charlist) {
            writeQ(characterData.getGuid());
            writeS(characterData.getName());
            writeC(characterData.getRace().getValue());
            writeC(characterData.getClazz().getValue());
            writeC(characterData.getGender().getValue());
            final int playerBytes = characterData.getPlayerBytes();
            writeC(playerBytes & 0xFF);
            writeC((playerBytes >> 8) & 0xFF);
            writeC((playerBytes >> 16) & 0xFF);
            writeC((playerBytes >> 24) & 0xFF);
            writeC(characterData.getPlayerBytes2() & 0xFF);
            writeC(characterData.getLevel());
            writeD(characterData.getMovement().getZone());
            writeD(characterData.getMovement().getMap());
            writeF(characterData.getMovement().getPosition().getX());
            writeF(characterData.getMovement().getPosition().getY());
            writeF(characterData.getMovement().getPosition().getZ());
            // TODO: implement guild
            writeD(-1);
            // Ban, dead, help, cloak, rename values. default: no flags
            writeD(0);
            writeD(characterData.getAtLoginFlag()); //

            writeC(characterData.getAtLoginFlag() & 0x1); // FIXME check at
                                                          // login first
            // TODO: implement Pet!
            writeD(0x00 /* character.getPetDisplayId() */);
            writeD(0x00 /* character.getPetLevel() */);
            writeD(0x00 /* character.getPetFamily() */);

            // final List<InventoryItem> inventory = character.getInventory();

            final ItemStorages itemStorages =
                    ServiceContent.getContext().getBean(ItemStorages.class);
            if (itemStorages == null) {
                logger.error("Cannot get ItemStorages instance!");
            }
            for (final EquipmentSlots slot : EquipmentSlots.values()) {
                final FieldsItem invItem = characterData.getInventory().get(slot.ordinal());

                if ((invItem != null)) {
                    int displayInfoID = 0x00;
                    byte inventoryType = 0;
                    try {
                        final ItemPrototype proto = itemStorages.get(invItem.getEntry());
                        displayInfoID = proto.getDisplayInfoID();
                        inventoryType = (byte) proto.getInventoryType().ordinal();
                    } catch (final Exception e) {
                        logger.error("ID not found in the storage: " + invItem.getEntry(), e);
                    }

                    writeD(displayInfoID);
                    writeC(inventoryType);
                    writeD(0x00);
                } else {
                    writeD(0x00);
                    writeC(0x00);
                    writeD(0x00);
                }
            }

        }

    }

}
