/**
 * 
 */
package org.jmangos.realm.entities;

import java.util.EnumSet;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jmangos.commons.enums.EquipmentSlots;
import org.jmangos.realm.model.base.update.PlayerFields;
import org.jmangos.realm.model.base.update.UnitField;
import org.jmangos.realm.model.enums.EnchantmentSlot;
import org.jmangos.realm.model.enums.Gender;
import org.jmangos.realm.model.enums.ItemBondingsCondition;
import org.jmangos.realm.model.enums.ItemFlags;
import org.jmangos.realm.model.enums.ModType;
import org.jmangos.realm.model.enums.Powers;
import org.jmangos.realm.model.enums.Stats;
import org.jmangos.realm.model.enums.TypeID;
import org.jmangos.realm.model.enums.TypeMask;
import org.jmangos.realm.model.enums.UnitFlags;
import org.jmangos.realm.model.enums.UnitFlags2;
import org.jmangos.realm.model.enums.UpdateType;

/**
 * @author MinimaJack
 * 
 */
@Entity
@Table(name = "fields_player")
public class CharacterData extends FieldsCharacter {

    @OneToOne(targetEntity = CharacterPositionerHolder.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    CharacterPositionerHolder movement;

    @Column(name = "atLoginFlag", nullable = true, insertable = true, updatable = true)
    private int atLoginFlag;

    /*
     * (non-Javadoc)
     * 
     * @see org.jmangos.test.subentities.UnitObject#setGender(java.lang.Byte)
     */
    @Override
    public void setGender(final Gender gender) {

        super.setGender(gender);
        setPlayerBytes3(getPlayerBytes3() | (0x1 & gender.getValue()));

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jmangos.test.subentities.BaseObject#initBits()
     */
    @Override
    public void initBits() {

        super.initBits();
        setType(getType() | TypeMask.PLAYER.getValue());

        final EnumSet<Powers> usedPower = EnumSet.of(Powers.HEALTH, getPowerType());
        for (final Powers powers : usedPower) {
            this.bitSet.set(UnitField.UNIT_FIELD_HEALTH.getValue() + powers.ordinal());
            this.bitSet.set(UnitField.UNIT_FIELD_MAXHEALTH.getValue() + powers.ordinal());
        }

        this.bitSet.set(PlayerFields.PLAYER_BYTES.getValue());
        this.bitSet.set(PlayerFields.PLAYER_BYTES_2.getValue());
        setFlags(UnitFlags.PVP_ATTACKABLE.getValue());
        setFlags2(UnitFlags2.UNIT_FLAG2_REGENERATE_POWER.getValue());
        setAttackTime(0, 2900);
        setAttackTime(1, 2000);
        setAttackTime(2, 2000);
        setBoundingRadius(0.306f);
        setCombatReach(1.5f);
        setMainDemage(0, 7.1428576f);
        setMainDemage(1, 9.142858f);
        setModCastSpeed(1f);
        setPlayerBytes3(0);
        setResistances(0, 45);
        setAttackPower(20);
        setRangedAttackPower(10);
        setSkillInfo(0, 54);
        setSkillInfo(1, 327681);
        setSkillInfo(3, 56);
        setSkillInfo(4, 65537);
        setSkillInfo(6, 95);
        setSkillInfo(7, 327681);
        setSkillInfo(9, 98);
        setSkillInfo(10, 19661100);
        setSkillInfo(12, 136);
        setSkillInfo(13, 327681);
        setSkillInfo(15, 162);
        setSkillInfo(16, 327681);
        setSkillInfo(18, 228);
        setSkillInfo(19, 327681);
        setSkillInfo(21, 415);
        setSkillInfo(22, 65537);
        setDodgeMul(4.8438873f);
        setCritPercentage(4.8405f);
        setRangedCritPercentage(4.8405f);
        setOffHandCritPercentage(4.8405f);
        setCharacterPoints2(2);
        for (int i = 0; i < 7; i++) {
            setModDamageDonePct(i, 1f);
        }
        for (int i = 1; i < 7; i++) {
            setSpellCritPercentage(i, 4.9995003f);
        }
        setExploredZones(3, 536870912);
        setModHealingDonePct(1f);
        setModHealingPct(1f);
        setModDamageDonePct(0, 1f);
        setRangedDemage(0, 2.4285715f);
        setRangedDemage(1, 3.4285715f);

        for (int i = 0; i < 4; i++) {
            this.bitSet.set(PlayerFields.PLAYER_RUNE_REGEN_1.getValue() + i);
        }

        for (int i = 0; i < 6; i++) {
            this.bitSet.set(PlayerFields.PLAYER_FIELD_GLYPH_SLOTS_1.getValue() + i);
        }

    }

    @Override
    public void initBitsForCollections() {

        super.initBitsForCollections();

        for (final Entry<Integer, FieldsItem> equips : getInventory().entrySet()) {
            final EquipmentSlots slot = EquipmentSlots.get(equips.getKey());
            if ((slot != null) && EquipmentSlots.ITEMS.contains(slot)) {
                this.bitSet.set(PlayerFields.PLAYER_VISIBLE_ITEM_1_ENTRYID.getValue() +
                    (slot.ordinal() * 2));
                if ((equips.getValue().getEnchants().get(EnchantmentSlot.PERM) != null) ||
                    (equips.getValue().getEnchants().get(EnchantmentSlot.TEMP) != null)) {
                    this.bitSet.set(PlayerFields.PLAYER_VISIBLE_ITEM_1_ENCHANTMENT.getValue() +
                        (slot.ordinal() * 2));
                }
            }
            this.bitSet.set(PlayerFields.PLAYER_FIELD_INV_SLOT_HEAD.getValue() +
                (equips.getKey() * 2));
            this.bitSet.set(PlayerFields.PLAYER_FIELD_INV_SLOT_HEAD.getValue() +
                (equips.getKey() * 2) +
                1);
        }
    }

    public void updateMaxPowers() {

        int value = 0;
        for (final Powers power : Powers.PLAYER_CREATE_POWERS) {
            value = getPowers().getCreatePower(power);

            value += getModifierValue(power, ModType.BASE_FLAT);
            value *= getModifierValue(power, ModType.BASE_PCT);
            value += getModifierValue(power, ModType.TOTAL_FLAT) + getBonusPowerValue(power);
            value *= getModifierValue(power, ModType.TOTAL_PCT);

            getPowers().setMaxPower(power, value);
        }
    }

    private int getBonusPowerValue(final Powers power) {

        switch (power) {
            case HEALTH:
                final int stamina = getStat(Stats.STAMINA);

                final int baseStam = stamina < 20 ? stamina : 20;
                final int moreStam = stamina - baseStam;

                return baseStam + (moreStam * 10);
            case MANA:
                final int intellect = getStat(Stats.INTELLECT);

                final int baseInt = (intellect < 20) ? intellect : 20;
                final int moreInt = intellect - baseInt;

                return baseInt + (moreInt * 15);
            default:
                return 0;
        }
    }

    @Override
    public BasicPositionerHolder getMovement() {

        return this.movement;
    }

    /**
     * @param movement
     *        the movement to set
     */
    public final void setMovement(final CharacterPositionerHolder movement) {

        this.movement = movement;
    }

    @Override
    public int buildCreateBlock(final ChannelBuffer updateBlock, final CharacterData characterData) {

        int count = 0;
        count += super.buildCreateBlock(updateBlock, characterData);
        if (this == characterData) {
            for (final Entry<Integer, FieldsItem> equips : getInventory().entrySet()) {
                count += equips.getValue().buildCreateBlock(updateBlock, characterData);
            }
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jmangos.test.subentities.BaseObject#getTypeId()
     */
    @Override
    protected int getTypeId() {

        return TypeID.PLAYER.getValue();
    }

    @Override
    protected UpdateType getCreateUpdateType() {

        return UpdateType.CREATE_OBJECT2;
    }

    public void equipItem(final EquipmentSlots equipmentSlots, final FieldsItem item) {

        item.setContained(getGuid());
        item.setOwner(getGuid());
        if (getInventory().containsKey(equipmentSlots.ordinal())) {
            final FieldsItem lastEquipItem = getInventory().get(equipmentSlots.ordinal());
            addToInventory(lastEquipItem);
        }
        /**
         * TODO fix crash client from hunter bugs now put it to pack
         */
        if (EquipmentSlots.BAGS.contains(equipmentSlots)) {
            addToInventory(item);
        } else {
            getInventory().put(equipmentSlots.ordinal(), item);

            if (EquipmentSlots.ITEMS.contains(equipmentSlots)) {
                this.bitSet.set(PlayerFields.PLAYER_VISIBLE_ITEM_1_ENTRYID.getValue() +
                    (equipmentSlots.ordinal() * 2));
                if ((item.getEnchants().get(EnchantmentSlot.PERM) != null) ||
                    (item.getEnchants().get(EnchantmentSlot.TEMP) != null)) {
                    this.bitSet.set(PlayerFields.PLAYER_VISIBLE_ITEM_1_ENCHANTMENT.getValue() +
                        (equipmentSlots.ordinal() * 2));
                }
            }
            this.bitSet.set(PlayerFields.PLAYER_FIELD_INV_SLOT_HEAD.getValue() +
                (equipmentSlots.ordinal() * 2));
            this.bitSet.set(PlayerFields.PLAYER_FIELD_INV_SLOT_HEAD.getValue() +
                (equipmentSlots.ordinal() * 2) +
                1);
        }
        if (item.getPrototype().getBonding() == ItemBondingsCondition.WHEN_EQUIPPED.ordinal()) {
            item.addFlags(ItemFlags.BINDED.getValue());
        }

    }

    public boolean addToInventory(final FieldsItem item) {

        item.setOwner(getGuid());
        final Integer slot = getFreeSlotInPack();
        boolean success = false;
        if (slot != null) {
            item.setContained(getGuid());
            getInventory().put(slot, item);
            success = true;
        }

        if (!success) {
            for (final EquipmentSlots bag : EquipmentSlots.BAGS) {
                if (getInventory().containsKey(bag.ordinal())) {
                    final FieldsContainer curbag =
                            (FieldsContainer) getInventory().get(bag.ordinal());
                    if (curbag.addToInventory(item)) {
                        success = true;
                        break;
                    }
                }
            }
        }

        if (success &&
            (item.getPrototype().getBonding() == ItemBondingsCondition.WHEN_PICKED_UP.ordinal())) {
            item.addFlags(ItemFlags.BINDED.getValue());
        }
        return success;
    }

    private Integer getFreeSlotInPack() {

        for (Integer i = 23; i < 40; i++) {
            if (!getInventory().containsKey(i)) {
                return i;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jmangos.test.subentities.ItemObject#writeValuesUpdate(java.nio.ByteBuffer
     * )
     */
    @Override
    public ChannelBuffer writeValuesUpdate() {

        final ChannelBuffer ocBuffer = super.writeValuesUpdate();
        if (this.bitSet.get(PlayerFields.PLAYER_DUEL_ARBITER.getValue())) {
            ocBuffer.writeLong(getDuelArbiter());
        };
        if (this.bitSet.get(PlayerFields.PLAYER_FLAGS.getValue())) {
            ocBuffer.writeInt(getPlayerFlags());
        };
        if (this.bitSet.get(PlayerFields.PLAYER_GUILDID.getValue())) {
            ocBuffer.writeInt(getGuildId());
        };
        if (this.bitSet.get(PlayerFields.PLAYER_GUILDRANK.getValue())) {
            ocBuffer.writeInt(getGuildRank());
        };
        if (this.bitSet.get(PlayerFields.PLAYER_BYTES.getValue())) {
            ocBuffer.writeInt(getPlayerBytes());
        };
        if (this.bitSet.get(PlayerFields.PLAYER_BYTES_2.getValue())) {
            ocBuffer.writeInt(getPlayerBytes2());
        };
        if (this.bitSet.get(PlayerFields.PLAYER_BYTES_3.getValue())) {
            ocBuffer.writeInt(getPlayerBytes3());
        };
        if (this.bitSet.get(PlayerFields.PLAYER_DUEL_TEAM.getValue())) {
            ocBuffer.writeInt(getDuelTeam());
        };
        if (this.bitSet.get(PlayerFields.PLAYER_GUILD_TIMESTAMP.getValue())) {
            ocBuffer.writeInt(getGuildTimeStamp());
        };
        for (int i = 0; i < 125; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_QUEST_LOG_1_1.getValue() + i)) {
                ocBuffer.writeInt(getQuestLog()[i]);
            }
        }
        for (final EquipmentSlots slot : EquipmentSlots.ITEMS) {

            if (this.bitSet.get(PlayerFields.PLAYER_VISIBLE_ITEM_1_ENTRYID.getValue() +
                (slot.ordinal() * 2))) {
                ocBuffer.writeInt(getInventory().get(slot.ordinal()).getEntry());
            };
            if (this.bitSet.get(PlayerFields.PLAYER_VISIBLE_ITEM_1_ENCHANTMENT.getValue() +
                (slot.ordinal() * 2))) {
                ItemEnchant enchant =
                        getInventory().get(slot.ordinal()).getEnchants().get(EnchantmentSlot.PERM);
                if (enchant != null) {
                    ocBuffer.writeShort(enchant.getEnchantId());
                } else {
                    ocBuffer.writeShort(0);
                }
                enchant =
                        getInventory().get(slot.ordinal()).getEnchants().get(EnchantmentSlot.TEMP);
                if (enchant != null) {
                    ocBuffer.writeShort(enchant.getEnchantId());
                } else {
                    ocBuffer.writeShort(0);
                }
            };
        }
        if (this.bitSet.get(PlayerFields.PLAYER_CHOSEN_TITLE.getValue())) {
            ocBuffer.writeInt(getChosenTitle());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FAKE_INEBRIATION.getValue())) {
            ocBuffer.writeInt(getFakeInebriation());
        }
        for (int i = 0; i < 150; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_INV_SLOT_HEAD.getValue() + (i * 2))) {
                ocBuffer.writeLong(getInventory().get(i).getGuid());
            }
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FARSIGHT.getValue())) {
            ocBuffer.writeLong(getFarsight());
        }
        for (int i = 0; i < 3; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER__FIELD_KNOWN_TITLES.getValue() + (i * 2))) {
                ocBuffer.writeLong(getKnownTitles()[i]);
            }
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_KNOWN_CURRENCIES.getValue())) {
            ocBuffer.writeLong(getKnownCurrencies());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_XP.getValue())) {
            ocBuffer.writeInt(getXp());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_NEXT_LEVEL_XP.getValue())) {
            ocBuffer.writeInt(getNextLevelXp());
        }
        for (int i = 0; i < 384; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_SKILL_INFO_1_1.getValue() + i)) {
                ocBuffer.writeInt(getSkillInfo(i));
            }
        }
        if (this.bitSet.get(PlayerFields.PLAYER_CHARACTER_POINTS1.getValue())) {
            ocBuffer.writeInt(getCharacterPoints1());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_CHARACTER_POINTS2.getValue())) {
            ocBuffer.writeInt(getCharacterPoints2());
        }

        if (this.bitSet.get(PlayerFields.PLAYER_TRACK_CREATURES.getValue())) {
            ocBuffer.writeInt(getTrackCreatures());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_TRACK_RESOURCES.getValue())) {
            ocBuffer.writeInt(getTrackResources());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_BLOCK_PERCENTAGE.getValue())) {
            ocBuffer.writeFloat(getBlockMul());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_DODGE_PERCENTAGE.getValue())) {
            ocBuffer.writeFloat(getDodgeMul());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_PARRY_PERCENTAGE.getValue())) {
            ocBuffer.writeFloat(getParryMul());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_EXPERTISE.getValue())) {
            ocBuffer.writeInt(getExperties());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_OFFHAND_EXPERTISE.getValue())) {
            ocBuffer.writeInt(getOffHandExperties());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_CRIT_PERCENTAGE.getValue())) {
            ocBuffer.writeFloat(getCritPercentage());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_RANGED_CRIT_PERCENTAGE.getValue())) {
            ocBuffer.writeFloat(getRangedCritPercentage());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_OFFHAND_CRIT_PERCENTAGE.getValue())) {
            ocBuffer.writeFloat(getOffHandCritPercentage());
        }

        for (int i = 0; i < 7; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_SPELL_CRIT_PERCENTAGE1.getValue() + i)) {
                ocBuffer.writeFloat(getSpellCritPercentage()[i]);
            }
        }
        if (this.bitSet.get(PlayerFields.PLAYER_SHIELD_BLOCK.getValue())) {
            ocBuffer.writeInt(getShieldBlock());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_SHIELD_BLOCK_CRIT_PERCENTAGE.getValue())) {
            ocBuffer.writeFloat(getShieldCritPercentage());
        }
        for (int i = 0; i < 128; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_EXPLORED_ZONES_1.getValue() + i)) {
                ocBuffer.writeInt(getExploredZones()[i]);
            }
        }
        if (this.bitSet.get(PlayerFields.PLAYER_REST_STATE_EXPERIENCE.getValue())) {
            ocBuffer.writeInt(getRestStateExperience());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_COINAGE.getValue())) {
            ocBuffer.writeInt(getMoney());
        }
        for (int i = 0; i < 7; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_MOD_DAMAGE_DONE_POS.getValue() + i)) {
                ocBuffer.writeInt(getModDamageDonePos()[i]);
            }
        }
        for (int i = 0; i < 7; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_MOD_DAMAGE_DONE_NEG.getValue() + i)) {
                ocBuffer.writeInt(getModDamageDoneNeg()[i]);
            }
        }
        for (int i = 0; i < 7; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_MOD_DAMAGE_DONE_PCT.getValue() + i)) {
                ocBuffer.writeFloat(getModDamageDonePct()[i]);
            }
        }

        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_MOD_HEALING_DONE_POS.getValue())) {
            ocBuffer.writeInt(getModHealingDonePos());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_MOD_HEALING_PCT.getValue())) {
            ocBuffer.writeFloat(getModHealingPct());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_MOD_HEALING_DONE_PCT.getValue())) {
            ocBuffer.writeFloat(getModHealingDonePct());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_MOD_TARGET_RESISTANCE.getValue())) {
            ocBuffer.writeFloat(getModTargetResistance());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_MOD_TARGET_PHYSICAL_RESISTANCE.getValue())) {
            ocBuffer.writeFloat(getModTargetPhysResistance());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_BYTES.getValue())) {
            ocBuffer.writeInt(getPlayerFieldBytes());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_AMMO_ID.getValue())) {
            ocBuffer.writeInt(getAmmoId());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_SELF_RES_SPELL.getValue())) {
            ocBuffer.writeInt(getSelfResSpell());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_PVP_MEDALS.getValue())) {
            ocBuffer.writeInt(getPvpMedals());
        }
        for (int i = 0; i < 12; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_BUYBACK_PRICE_1.getValue() + i)) {
                ocBuffer.writeInt(getBaybackPrice()[i]);
            }
        }
        for (int i = 0; i < 12; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_BUYBACK_TIMESTAMP_1.getValue() + i)) {
                ocBuffer.writeInt(getBaybackPrice()[i]);
            }
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_KILLS.getValue())) {
            // TODO fix kills to short
            ocBuffer.writeInt(getKills());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_TODAY_CONTRIBUTION.getValue())) {
            ocBuffer.writeInt(getTodayContribution());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_YESTERDAY_CONTRIBUTION.getValue())) {
            ocBuffer.writeInt(getYesterdayContribution());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_LIFETIME_HONORBALE_KILLS.getValue())) {
            ocBuffer.writeInt(getKills());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_BYTES2.getValue())) {
            ocBuffer.writeInt(getPlayerBytes2());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_WATCHED_FACTION_INDEX.getValue())) {
            ocBuffer.writeInt(getWatchedFactionIndex());
        }
        for (int i = 0; i < 25; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_COMBAT_RATING_1.getValue() + i)) {
                ocBuffer.writeInt(getCombatRating()[i]);
            }
        }
        for (int i = 0; i < 21; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_ARENA_TEAM_INFO_1_1.getValue() + i)) {
                ocBuffer.writeInt(getArenaTeamInfo()[i]);
            }
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_HONOR_CURRENCY.getValue())) {
            ocBuffer.writeInt(getHonorCurrency());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_ARENA_CURRENCY.getValue())) {
            ocBuffer.writeInt(getArenaCurrency());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_FIELD_MAX_LEVEL.getValue())) {
            ocBuffer.writeInt(getMaxLevel());
        }
        for (int i = 0; i < 25; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_DAILY_QUESTS_1.getValue() + i)) {
                ocBuffer.writeInt(getDailyQuests()[i]);
            }
        }
        for (int i = 0; i < 4; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_RUNE_REGEN_1.getValue() + i)) {
                ocBuffer.writeFloat(getRuneRegen()[i]);
            }
        }
        for (int i = 0; i < 3; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_NO_REAGENT_COST_1.getValue() + i)) {
                ocBuffer.writeInt(getNoReagentCost()[i]);
            }
        }
        for (int i = 0; i < 6; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_GLYPH_SLOTS_1.getValue() + i)) {
                ocBuffer.writeInt(getGlyphSlots()[i]);
            }
        }
        for (int i = 0; i < 6; i++) {
            if (this.bitSet.get(PlayerFields.PLAYER_FIELD_GLYPHS_1.getValue() + i)) {
                ocBuffer.writeInt(getGlyph()[i]);
            }
        }
        if (this.bitSet.get(PlayerFields.PLAYER_GLYPHS_ENABLED.getValue())) {
            ocBuffer.writeInt(getGlyphsEnabled());
        }
        if (this.bitSet.get(PlayerFields.PLAYER_PET_SPELL_POWER.getValue())) {
            ocBuffer.writeInt(getPetSpellPower());
        }
        return ocBuffer;
    }

    /**
     * @return the atLoginFlag
     */
    public int getAtLoginFlag() {

        return this.atLoginFlag;
    }

    /**
     * @param atLoginFlag
     *        the atLoginFlag to set
     */
    public void setAtLoginFlag(final int atLoginFlag) {

        this.atLoginFlag = atLoginFlag;
    }
}
