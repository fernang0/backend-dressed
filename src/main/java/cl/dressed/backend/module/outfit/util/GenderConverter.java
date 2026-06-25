package cl.dressed.backend.module.outfit.util;

/**
 * Converter para mapear géneros entre perfil (inglés) y prendas (español).
 * El perfil del usuario almacena géneros en inglés (male, female)
 * mientras que las prendas almacenan géneros en español (hombre, mujer).
 */
public class GenderConverter {

    /**
     * Convierte el gender del perfil (inglés) al gender de las prendas (español).
     * male → hombre
     * female → mujer
     *
     * @param profileGender Gender del perfil del usuario (ej: "male", "female")
     * @return Gender en español para buscar prendas (ej: "hombre", "mujer")
     * @throws IllegalArgumentException si el gender no es reconocido
     */
    public static String convertProfileGenderToGarmentGender(String profileGender) {
        if (profileGender == null) {
            return null;
        }

        return switch (profileGender.toLowerCase().trim()) {
            case "male" -> "hombre";
            case "female" -> "mujer";
            default -> throw new IllegalArgumentException(
                "Gender no soportado: " + profileGender +
                ". Use 'male' o 'female'."
            );
        };
    }

    /**
     * Convierte el gender de la prenda (español) al gender del perfil (inglés).
     * hombre → male
     * mujer → female
     *
     * @param garmentGender Gender de la prenda (ej: "hombre", "mujer")
     * @return Gender en inglés (ej: "male", "female")
     * @throws IllegalArgumentException si el gender no es reconocido
     */
    public static String convertGarmentGenderToProfileGender(String garmentGender) {
        if (garmentGender == null) {
            return null;
        }

        return switch (garmentGender.toLowerCase().trim()) {
            case "hombre" -> "male";
            case "mujer" -> "female";
            default -> throw new IllegalArgumentException(
                "Gender no soportado: " + garmentGender +
                ". Use 'hombre' o 'mujer'."
            );
        };
    }
}
