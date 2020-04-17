package net.stevenrafferty.headhunting.utils;

import org.bukkit.entity.EntityType;

public class MobHeads {

  public boolean isType(EntityType type) {
    boolean toReturn = false;
    switch (type) {
      case BLAZE:
      case COW:
      case IRON_GOLEM:
      case PIG:
      case SKELETON:
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
      case COW: {
        toReturn = "MHF_Cow";
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
      case SKELETON: {
        toReturn = "MHF_Skeleton";
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
