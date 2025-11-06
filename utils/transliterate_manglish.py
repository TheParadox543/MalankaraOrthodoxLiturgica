import re

malayalam_to_latin_vowels = {
    "അ": "a",
    "ആ": "aa",
    "ഇ": "i",
    "ഈ": "ee",
    "ഉ": "u",
    "ഊ": "oo",
    "ഋ": "r",
    "എ": "e",
    "ഏ": "e",
    "ഐ": "ai",
    "ഒ": "o",
    "ഓ": "o",
    "ഔ": "au",
}
malayalam_to_latin_consonants = {
    "ക": "ka",
    "ഖ": "kha",
    "ഗ": "ga",
    "ഘ": "gha",
    "ങ": "nga",
    "ച": "cha",
    "ഛ": "chha",
    "ജ": "ja",
    "ഝ": "jha",
    "ഞ": "nya",
    "ട": "da",
    "ഠ": "dha",
    "ഡ": "da",
    "ഢ": "dha",
    "ണ": "na",
    "ത": "tha",
    "ഥ": "thha",
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
    "ശ": "sha",
    "ഷ": "sha",
    "സ": "sa",
    "ഹ": "ha",
    "ള": "lla",
    "ഴ": "zha",
    "റ": "ra",
}
malayalam_to_latin_vowel_marks = {
    "് ": "u ",
    "്": "",
    "ാ": "aa",
    "ി": "i",
    "ീ": "ee",
    "ു": "u",
    "ൂ": "oo",
    "ൃ": "r",
    "െ": "e",
    "േ": "e",
    "ൈ": "ai",
    "ൊ": "o",
    "ോ": "o",
    "ൌ": "au",
    "ൗ": "au",
    "ം": "am",
    "ഃ": "h",
}
malayalam_to_latin_chillus = {
    "ാ": "aa",
    "ം": "m",
    "ർ": "r",
    "ൽ": "l",
    "ൾ": "ll",
    "ൺ": "n",
    "ൻ": "n",
}
known_clusters = {
    "ൻ്റെ": "nte",
    "ന്റെ": "nte",
    "ഷ്‌ട": "shta",
    "ക്ക": "kka",
    "ഗ്ഗ": "ga",
    "ങ്ങ": "nga",
    "ച്ച": "cha",
    "ജ്ജ": "ja",
    "ഞ്ച": "ncha",
    "ഞ്ഞ": "nya",
    "ട്ട": "tta",
    "ത്ത": "ttha",
    "ണ്ട": "nda",
    "ന്ത": "nta",
    "ന്ധ": "ndha",
    "പ്പ": "ppa",
    "മ്മ": "mma",
    "ല്ല": "lla",
    "വ്വ": "vva",
    "സ്സ": "ssa",
    "റ്റ": "tta",
    # add more as you encounter them...
}


def custom_manglish_transliterate(text: str) -> str:
    """Transliterate the given text using a custom mapping.

    Args:
        text (str): The text to transliterate.

    Returns:
        str: The transliterated text.
    """
    result = text.replace("\u200b", "").replace("‌", "")  # Remove zero-width spaces

    # Handle known clusters first
    for cluster, latin in known_clusters.items():
        for diacritic, latin_diacritic in malayalam_to_latin_vowel_marks.items():
            result = result.replace(cluster + diacritic, latin[:-1] + latin_diacritic)
        result = result.replace(cluster, latin)

    # Handle consonant + vowel markers
    for con, consonant in malayalam_to_latin_consonants.items():
        for diacritic, latin in malayalam_to_latin_vowel_marks.items():
            result = result.replace(con + diacritic, consonant[:-1] + latin)
        result = result.replace(con, consonant)

    # Handle chillu letters
    for chillu, latin in malayalam_to_latin_chillus.items():
        result = result.replace(chillu, latin)

    # Handle standalone vowels
    for vow, latin in malayalam_to_latin_vowels.items():
        result = result.replace(vow, latin)

    result = capitalize_transliterated(result)

    return result


def capitalize_transliterated(text: str) -> str:
    # Define dictionary of proper nouns to always capitalize
    proper_nouns = {
        "daivam": "Daivam",
        "yeshu": "Yeshu",
        "mariya": "Mariya",
        "kristu": "Kristu",
        "halleluiah": "Halleluiah",
    }

    # # Lowercase everything first for consistency
    # text = text.lower()

    # Capitalize proper nouns everywhere
    for word, cap_word in proper_nouns.items():
        text = re.sub(rf"\b{word}\b", cap_word, text)

    # Pass 1: capitalize first letter after ". " or "! "
    text = re.sub(
        r"([.!:]\s*/?\s*)([a-z])",
        lambda m: m.group(1) + m.group(2).upper(),
        text,
    )

    # Pass 2: capitalize first letter after newline
    text = re.sub(
        r"(\n\s*)([a-z])",
        lambda m: m.group(1) + m.group(2).upper(),
        text,
    )

    # Capitalize very first character of text
    if text:
        text = text[0].upper() + text[1:]

    return text


def read_file(file: str) -> str:
    """Read the contents of a text file.

    Args:
        file (str): The path to the text file.

    Returns:
        str: The contents of the text file.
    """
    with open(file, "r", encoding="utf-8") as f:
        return f.read()


def clear_text_file():
    """Clear the contents of the output.txt file."""
    with open("output.txt", "w", encoding="utf-8") as f:
        f.write("")  # Clear the file


def write_text_to_file(text: str):
    """Write text to the output.txt file, overwriting existing content.

    Args:
        text (str): The text to write.
    """
    with open("output.txt", "w", encoding="utf-8") as f:
        f.write(text)


def append_text_to_file(text: str):
    """Append text to the output.txt file.

    Args:
        text (str): The text to append.
    """
    with open("output.txt", "a", encoding="utf-8") as f:
        f.write(text + "\n")


if __name__ == "__main__":
    input_text = read_file("sample_text.txt")
    transliterated_text = custom_manglish_transliterate(input_text)

    transliterated_text = capitalize_transliterated(transliterated_text)
    write_text_to_file(transliterated_text)
