package net.stevenrafferty.headhunting.utils;

import org.bukkit.entity.EntityType;

public class MobHeads {

  public boolean isType(EntityType type) {
    boolean toReturn = false;
    switch (type) {
      case BLAZE:
      case CAVE_SPIDER:
      case CHICKEN:
      case COW:
      case CREEPER:
      case ENDERMAN:
      case GHAST:
      case IRON_GOLEM:
      case PIG:
      case SHEEP:
      case MUSHROOM_COW:
      case SKELETON:
      case SLIME:
      case SPIDER:
      case ZOMBIE: {
        toReturn = true;
        break;
      }
    }
    return toReturn;
  }

  public String getOwnerOfType(EntityType type) {
    String toReturn = null;
    switch (type) {
      case BLAZE: {
        toReturn = "MHF_Blaze";
        break;
      }
      case CAVE_SPIDER: {
        toReturn = "MHF_CaveSpider";
        break;
      }
      case CHICKEN: {
        toReturn = "MHF_Chicken";
        break;
      }
      case COW: {
        toReturn = "MHF_Cow";
        break;
      }
      case CREEPER: {
        toReturn = "MHF_Creeper";
        break;
      }
      case ENDERMAN: {
        toReturn = "MHF_Enderman";
        break;
      }
      case GHAST: {
        toReturn = "MHF_Ghast";
        break;
      }
      case IRON_GOLEM: {
        toReturn = "MHF_Golem";
        break;
      }
      case PIG: {
        toReturn = "MHF_Pig";
        break;
      }
      case SHEEP: {
        toReturn = "MHF_Sheep";
        break;
      }
      case MUSHROOM_COW: {
        toReturn = "MHF_MushroomCow";
        break;
      }
      case SKELETON: {
        toReturn = "MHF_Skeleton";
        break;
      }
      case SLIME: {
        toReturn = "MHF_Slime";
        break;
      }
      case SPIDER: {
        toReturn = "MHF_Spider";
        break;
      }
      case ZOMBIE: {
        toReturn = "MHF_Zombie";
        break;
      }
    }
    return toReturn;
  }

}
