import json
import re
from docx import Document
from os import path, listdir, makedirs
from pprint import pprint

available_types = [
    "section",
    "link",
    "linkcollapsible",
    "collapsible",
    "endcollapsible",
    "title",
    "heading",
    "subheading",
    "song",
    "prose",
    "subtext",
]


def process_docx_file(
    working_directory: str,
    original_folder_path: str,
    new_folder_path: str,
    file_item_with_extension: str,
    item_path: str,
):
    # if the file is a docx file, convert it to json
    json_path_str = path.splitext(file_item_with_extension)[0]
    language, *json_path_strs, file_name = json_path_str.split("_")

    # Get relative path to the file and construct the new path
    relative_path_from_base = path.relpath(working_directory, original_folder_path)
    json_path = path.join(new_folder_path, language, relative_path_from_base, file_name)

    # Passing the paths to the convert function
    convert_doc_to_json(item_path, json_path)


def recursively_loop_through_files(
    working_directory: str,
    original_folder_path: str = "DOCS",
    new_folder_path: str = "JSON",
):
    if not path.isdir(working_directory):
        print(f"Error: {working_directory} is not a directory")
        return
    for item_name in listdir(working_directory):
        item_path = path.join(working_directory, item_name)
        if item_name.endswith(".docx") and "_" in item_name:
            # if the file is a docx file, convert it to json
            json_path_str = path.splitext(item_name)[0]
            language, *json_path_strs, file_name = json_path_str.split("_")

            # Get relative path to the file and construct the new path
            relative_path_from_base = path.relpath(
                working_directory, original_folder_path
            )
            json_path = path.join(
                new_folder_path,
                language,
                relative_path_from_base,
                file_name,
            )

            # Passing the paths to the convert function
            convert_doc_to_json(item_path, json_path)
        elif path.isdir(item_path):
            # if the file is a folder, recursively call the function
            recursively_loop_through_files(
                item_path,
                original_folder_path,
                new_folder_path,
            )


def convert_doc_to_json(doc_path: str, json_path: str):
    doc = Document(doc_path)
    data = []
    current_type: str | None = None
    is_collapsible = False
    current_song: str = ""
    section = ""

    for para in doc.paragraphs:
        line = para.text.strip()
        new_para = len(line) == 0
        if new_para and current_song:
            if is_collapsible:
                data[-1]["items"].append(
                    {
                        "type": current_type,
                        "content": current_song,
                    }
                )
            else:
                data.append(
                    {
                        "type": current_type,
                        "content": current_song,
                    }
                )
            current_song = ""
        if not line:
            continue

        match = re.match(r"^(\w+):\s*(.+)", line)
        if match:
            if current_type == "song" and current_song:
                if is_collapsible:
                    data[-1]["items"].append(
                        {
                            "type": current_type.lower(),
                            "content": current_song,
                        }
                    )
                else:
                    data.append(
                        {
                            "type": current_type.lower(),
                            "content": current_song,
                        }
                    )
                    current_song = ""

            temp_type, content = match.groups()
            temp_type_lower = temp_type.strip().lower()
            if temp_type_lower in available_types:
                current_type = temp_type_lower
            else:
                content = temp_type + ": " + content
                if current_type is None:
                    current_type = "prose"

            if current_type == "section":
                # If data is not empty, save it to a file
                if data:
                    finish_section(
                        current_type, current_song, data, section, json_path, True
                    )
                    data = []
                    current_type = None
                    current_song = ""
                section = content
            elif current_type == "endcollapsible":
                is_collapsible = False
            elif current_type == "link":
                data.append(
                    {
                        "type": current_type,
                        "file": content,
                    }
                )
            elif current_type == "linkcollapsible":
                data.append(
                    {
                        "type": "link-collapsible",
                        "file": content,
                    }
                )
            elif current_type == "collapsible":
                data.append(
                    {
                        "type": "collapsible-block",
                        "title": content,
                        "items": [],
                    }
                )
                is_collapsible = True
            elif current_type == "song":
                current_song = content  # Start first stanza
            elif current_type in available_types:
                if is_collapsible:
                    # If the last item was a collapsible, append to its items
                    try:
                        data[-1]["items"].append(
                            {
                                "type": current_type,
                                "content": content,
                            }
                        )
                    except Exception as e:
                        print(
                            "file:",
                            json_path,
                            "data:",
                            current_type,
                            is_collapsible,
                        )
                        pprint(data[-2])
                        raise e
                else:
                    data.append(
                        {
                            "type": current_type,
                            "content": content,
                        }
                    )
            else:
                raise ValueError(f"Unrecognized type '{temp_type}' in file {doc_path}.")
        elif current_type == "song":
            if current_song != "":
                current_song += "\n" + line  # Append line to stanza
            else:
                current_song += line
        else:
            if is_collapsible:
                # If the last item was a collapsible, append to its items
                try:
                    data[-1]["items"].append(
                        {
                            "type": current_type,
                            "content": line,
                        }
                    )
                except Exception as e:
                    print("file:", json_path)
                    raise e
            else:
                # try:
                data.append(
                    {
                        "type": current_type,
                        "content": line,
                    }
                )
                # except Exception as e:
                #     print(
                #         f"Error: {e}, filename: {json_path}, line: {line}, current_type: {current_type}",
                #     )
    finish_section(current_type, current_song, data, section, json_path)


def finish_section(
    current_type: str | None,
    current_song: str,
    data: list,
    section: str,
    json_path: str,
    new_section: bool = False,
):
    # Save the last song if it exists
    if current_type == "song" and current_song:
        data.append(
            {
                "type": current_type.lower(),
                "content": current_song,
            }
        )

    if section != "":
        json_path = path.join(json_path, section)
    filename = f"{json_path}.json"
    makedirs(path.dirname(filename), exist_ok=True)
    try:
        with open(filename, "w", encoding="utf-8") as json_file:
            json.dump(data, json_file, ensure_ascii=False, indent=4)
    except Exception as e:
        print(f"Error saving JSON file: {e}")
    else:
        # If the file was saved successfully, print a message
        print(f"Conversion complete. JSON saved to {filename}")


# Example usage
# convert_doc_to_json("DOCS/SleebaMalayalam.docx", "test")
if __name__ == "__main__":
    recursively_loop_through_files("DOCS/sacraments", new_folder_path="JSON/prayers")
