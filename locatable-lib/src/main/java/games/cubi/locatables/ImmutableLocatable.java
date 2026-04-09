package games.cubi.locatables;

public non-sealed interface ImmutableLocatable extends Locatable {
     default boolean isMutable() {
         return false;
     }
     
     default MutableLocatable castToMutableOrNull() {
         return null;
     }
}
