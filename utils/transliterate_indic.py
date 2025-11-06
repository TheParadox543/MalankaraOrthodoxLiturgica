# ISO 15919 Transliteration for Malayalam (No manual clusters)

from indic_transliteration import sanscript
from indic_transliteration.sanscript import SchemeMap, SCHEMES, transliterate

from transliterate_manglish import (
    read_file,
    clear_text_file,
    append_text_to_file,
    write_text_to_file,
)

mal_iso_vowels = {
    "അ": "a",
    "ആ": "ā",
    "ഇ": "i",
    "ഈ": "ī",
    "ഉ": "u",
    "ഊ": "ū",
    "ഋ": "ṛ",
    "എ": "e",
    "ഏ": "ē",
    "ഐ": "ai",
    "ഒ": "o",
    "ഓ": "ō",
    "ഔ": "au",
}

mal_iso_consonants = {
    "ക": "ka",
    "ഖ": "kha",
    "ഗ": "ga",
    "ഘ": "gha",
    "ങ": "ṅa",
    "ച": "ca",
    "ഛ": "cha",
    "ജ": "ja",
    "ഝ": "jha",
    "ഞ": "ña",
    "ട": "ṭa",
    "ഠ": "ṭha",
    "ഡ": "ḍa",
    "ഢ": "ḍha",
    "ണ": "ṇa",
    "ത": "ta",
    "ഥ": "tha",
    "ദ": "da",
    "ധ": "dha",
    "ന": "na",
    "പ": "pa",
    "ഫ": "pha",
    "ബ": "ba",
    "ഭ": "bha",
    "മ": "ma",
    "യ": "ya",
    "ര": "ra",
    "ല": "la",
    "വ": "va",
    "ശ": "śa",
    "ഷ": "ṣa",
    "സ": "sa",
    "ഹ": "ha",
    "ള": "ḷa",
    "ഴ": "ḻa",
    "റ": "ṟa",
}

mal_iso_vowel_symbols = {
    "് ": "ú ",
    "്": "",  # virāma
    "ാ": "ā",
    "ി": "i",
    "ീ": "ī",
    "ു": "u",
    "ൂ": "ū",
    "ൃ": "ṛ",
    "െ": "e",
    "േ": "ē",
    "ൈ": "ai",
    "ൊ": "o",
    "ോ": "ō",
    "ൌ": "au",
    "ം": "ṁ",
    "ഃ": "ḥ",
}

mal_iso_virama = {
    "്": "",  # virāma
}

mal_iso_anusvara = {
    "ം": "ṁ",
}

mal_iso_chillus = {
    "ൺ": "ṇ",
    "ൻ": "ṉ",
    "ർ": "ṟ",
    "ൽ": "l",
    "ൾ": "ḷ",
}

mal_special_clusters = {
    "nṟe": "nđe",
    "ṟṟ": "ŧŧ",
    "ḻ": "z",
}


def custom_iso_transliterate(text: str) -> str:
    """ISO 15919 transliteration for Malayalam without cluster mapping."""
    result = text

    # 1. Consonant + diacritic
    for con, latin_con in mal_iso_consonants.items():
        for diacritic, latin_vow_symbols in mal_iso_vowel_symbols.items():
            result = result.replace(con + diacritic, latin_con[:-1] + latin_vow_symbols)
        result = result.replace(con, latin_con)

    # 2. Chillus
    for chillu, latin_chillu in mal_iso_chillus.items():
        result = result.replace(chillu, latin_chillu)

    # 3. Anusvara
    for anusvara, latin_anusvara in mal_iso_anusvara.items():
        result = result.replace(anusvara, latin_anusvara)

    # 4. Standalone vowels
    for vow, latin_vow in mal_iso_vowels.items():
        result = result.replace(vow, latin_vow)

    return result


def iso_transliterate(text: str) -> str:
    """Transliterate the given Malayalam text to Latin script using ISO 15919.

    Args:
        text (str): The Malayalam text to transliterate.

    Returns:
        str: The transliterated text in Latin script.
    """
    scheme_map = SchemeMap(SCHEMES["malayalam"], SCHEMES["iso"])
    result = transliterate(text, scheme_map=scheme_map)

    # Handle chillus manually to add diacritics
    for chillu, latin_chillu in mal_iso_chillus.items():
        result = result.replace(chillu, latin_chillu)  # Adding acute accent for chillu

    # Handle special clusters
    for cluster, latin in mal_special_clusters.items():
        result = result.replace(cluster, latin)

    return result


# Example
if __name__ == "__main__":
    sample = read_file("sample_text.txt")
    transliterated = custom_iso_transliterate(sample)
    write_text_to_file(transliterated)

    transliterated = iso_transliterate(sample)
    append_text_to_file("\n\nUsing indic_transliteration library:\n")
    append_text_to_file(transliterated)
