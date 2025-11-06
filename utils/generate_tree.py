import json
import os
from pathlib import Path
from pprint import pprint

LANGUAGES = [
    "ml",
    "mn",
    "indic",
    "en",
]
HOURS = [
    "vespers",
    "compline",
    "matins",
    "prime",
    "terce",
    "sext",
    "none",
]
DAYS = [
    "monday",
    "tuesday",
    "wednesday",
    "thursday",
    "friday",
    "saturday",
]


class TreeNode:
    def __init__(
        self,
        route: str,
        parent: str | None,
        filename: str | None = None,
        children: list = [],
    ):
        self.route = route
        self.parent = parent
        self.filename = filename
        self.children: list[TreeNode] = children
        self.languages: list[str] = []

    def add_child(self, child_node: "TreeNode"):
        self.children.append(child_node)

    def to_dict(self) -> dict:
        if self.filename is not None or self.children == []:
            return {
                "route": self.route,
                "parent": self.parent,
                "filename": self.filename,
                "languages": self.languages,
                "children": [],
            }
        else:
            return {
                "route": self.route,
                "parent": self.parent,
                "filename": self.filename,
                "languages": self.languages,
                "children": [child.to_dict() for child in self.children],
            }


def common_prayers_section():
    current_route = "commonPrayers"
    return TreeNode(
        "commonPrayers",
        "malankara",
        children=[
            TreeNode("lords", current_route, f"{current_route}/lords.json"),
            TreeNode("mary", current_route, f"{current_route}/mary.json"),
            TreeNode("kauma", current_route, f"{current_route}/kauma.json"),
            TreeNode(
                "trisagionSyriac",
                current_route,
                f"{current_route}/trisagionSyriac.json",
            ),
            TreeNode(
                "niceneCreed",
                current_route,
                f"{current_route}/niceneCreed.json",
            ),
            TreeNode(
                "praiseOfAngels",
                current_route,
                f"{current_route}/praiseOfAngels.json",
            ),
            TreeNode(
                "praiseOfCherubims",
                current_route,
                f"{current_route}/praiseOfCherubims.json",
            ),
            TreeNode(
                "morningPraise",
                current_route,
                f"{current_route}/morningPraise.json",
            ),
            TreeNode(
                "cyclicPrayers",
                current_route,
                f"{current_route}/cyclicPrayers.json",
            ),
        ],
    )


def home_prayers_section():
    current_route = "homePrayers"
    parent_route = "dailyPrayers"
    current_path = "dailyPrayers/homePrayers"
    children: list[TreeNode] = []
    for child in ["vespers", "matins", "prime"]:
        children.append(
            TreeNode(
                f"{current_route}_{child}",
                current_route,
                f"{current_path}/{child}.json",
            )
        )
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def sleeba_section():
    current_route = "sleeba"
    parent_route = "dailyPrayers"
    current_path = "dailyPrayers/sleeba"
    chidren: list[TreeNode] = []
    new_hours = HOURS.copy()
    new_hours.pop()
    for child in new_hours:
        chidren.append(
            TreeNode(
                f"{current_route}_{child}",
                current_route,
                f"{current_path}/{child}.json",
            )
        )
    return TreeNode(
        current_route,
        parent_route,
        children=chidren,
    )


def kyamtha_section():
    current_route = "kyamtha"
    parent_route = "dailyPrayers"
    current_path = "dailyPrayers/kyamtha"
    children: list[TreeNode] = []
    for child in HOURS:
        children.append(
            TreeNode(
                f"{current_route}_{child}",
                current_route,
                f"{current_path}/{child}.json",
            )
        )
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def sheema_promiyon_section():
    current_route = "promiyon"
    parent_route = "sheema"
    current_path = "dailyPrayers/sheema/promiyon"
    children: list[TreeNode] = []
    for child in [
        "sheemaMary",
        "sheemaSleeba",
        "sheemaSaints",
        "sheemaApostle",
    ]:
        children.append(
            TreeNode(
                child,
                current_route,
                f"{current_path}/{child}.json",
            )
        )
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def sheema_section():
    current_route = "sheema"
    parent_route = "dailyPrayers"
    current_path = "dailyPrayers/sheema"
    children: list[TreeNode] = []
    for day in DAYS:
        day_children: list[TreeNode] = []
        for hour in HOURS:
            day_children.append(
                TreeNode(
                    f"{current_route}_{day}_{hour}",
                    f"{current_route}_{day}",
                    f"{current_path}/{day}/{hour}.json",
                )
            )
        children.append(
            TreeNode(
                f"{current_route}_{day}",
                current_route,
                children=day_children,
            )
        )
    children.append(sheema_promiyon_section())
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def daily_prayers_section():
    current_route = "dailyPrayers"
    return TreeNode(
        current_route,
        "malankara",
        children=[
            home_prayers_section(),
            sleeba_section(),
            kyamtha_section(),
            sheema_section(),
        ],
    )


def qurbana_songs_section():
    current_route = "qurbanaSongs"
    parent_route = "qurbana"
    current_path = "qurbanaSongs"
    children: list[TreeNode] = []
    qurbana_song_sections = [
        "sanctification",
        "dedication",
        "revelationToZachariah",
        "annunciationToStMary",
        "visitationToElizabeth",
        "birthOfJohnTheBaptist",
        "revelationToJoseph",
        "sundayBeforeChristmas",
        "yeldho",
        "glorificationOfStMary",
        "massacreOfTheInfants",
        "afterChristmas",
        "circumcision",
        "epiphany",
        "beheadingOfStJohnTheBaptist",
        "feastOfStStephen",
        "afterEpiphany",
        "mayaltho",
        "ninevehLent",
        "allDepartedPriests",
        "allDepartedFaithful",
        "kothine",
        "lepersSunday",
        "palsySunday",
        "canaaniteWoman",
        "midLent",
        "crippledWoman",
        "blindMan",
        "forthiethFriday",
        "lazarusSaturday",
        "memoryOfFortyMartyrs",
        "palmSunday",
        "maundyThursday",
        "saturdayOfAnnunciation",
        "easter",
        "hevorae",
        "newSunday",
        "afterNewSunday",
        "ascension",
        "pentecost",
        "goldenFriday",
        "feastOfApostlesFast",
        "transfiguration",
        "assumption",
        "afterAssumption",
        "feastOfHolyCross",
        "afterHolyCross",
    ]
    for child in qurbana_song_sections:
        children.append(
            TreeNode(
                f"{current_route}_{child}",
                current_route,
                f"{current_path}/{child}/{child}Songs.json",
            )
        )
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def qurbana_section():
    current_route = "qurbana"
    parent_route = "sacraments"
    current_path = "sacraments/qurbana"
    children: list[TreeNode] = []
    for part in [
        "preparation",
        "partOne",
        "chapterOne",
        "chapterTwo",
        "chapterThree",
        "chapterFour",
        "chapterFive",
        "conclusion",
    ]:
        children.append(
            TreeNode(
                f"{current_route}_{part}",
                current_route,
                f"{current_path}/{part}.json",
            )
        )
    children.append(qurbana_songs_section())
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def wedding_section():
    current_route = "wedding"
    parent_route = "sacraments"
    current_path = "sacraments/wedding"
    children: list[TreeNode] = []
    for child in ["ring", "crown"]:
        children.append(
            TreeNode(
                f"{current_route}_{child}",
                current_route,
                f"{current_path}/{child}.json",
            )
        )
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def funeral_section():
    current_route = "funeral"
    parent_route = "sacraments"
    current_path = "sacraments/funeral"
    children: list[TreeNode] = []
    for gender in ["men", "women"]:
        gender_children: list[TreeNode] = []
        for part in ["firstPart", "secondPart", "thirdPart", "fourthPart"]:
            gender_children.append(
                TreeNode(
                    f"{current_route}_{gender}_{part}",
                    f"{current_route}_{gender}",
                    f"{current_path}/{gender}/{part}.json",
                )
            )
        children.append(
            TreeNode(
                f"{current_route}_{gender}",
                current_route,
                children=gender_children,
            )
        )
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def sacraments_section():
    return TreeNode(
        "sacraments",
        "malankara",
        children=[
            qurbana_section(),
            TreeNode(
                "baptism",
                "sacraments",
                "sacraments/baptism.json",
            ),
            wedding_section(),
            TreeNode(
                "houseWarming",
                "sacraments",
                "sacraments/houseWarming.json",
            ),
            funeral_section(),
            TreeNode(
                "anointment",
                "sacraments",
                "sacraments/anointment.json",
            ),
        ],
    )


def christmas_section():
    current_route = "christmas"
    parent_route = "feasts"
    current_path = "feasts/christmas"
    children: list[TreeNode] = []
    new_hours = HOURS.copy()
    new_hours.insert(3, "worshipOfCross")
    new_hours.pop(-1)
    for part in new_hours:
        children.append(
            TreeNode(
                f"{current_route}_{part}",
                current_route,
                f"{current_path}/{part}.json",
            )
        )
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def pentecost_section():
    current_route = "pentecost"
    parent_route = "feasts"
    current_path = "feasts/pentecost"
    children: list[TreeNode] = []
    for part in ["partOne", "partTwo", "partThree"]:
        children.append(
            TreeNode(
                f"{current_route}_{part}",
                current_route,
                f"{current_path}/{part}.json",
            )
        )
    return TreeNode(
        current_route,
        parent_route,
        children=children,
    )


def feastsSection():
    current_route = "feasts"
    return TreeNode(
        "feasts",
        "malankara",
        children=[
            christmas_section(),
            TreeNode(
                "epiphany",
                current_route,
                "feasts/epiphany.json",
            ),
            TreeNode(
                "reconciliationService",
                current_route,
                "feasts/reconciliationService.json",
            ),
            TreeNode(
                "halfLent",
                current_route,
                "feasts/halfLent.json",
            ),
            TreeNode(
                "ascension",
                current_route,
                "feasts/ascension.json",
            ),
            pentecost_section(),
        ],
    )


def contextualSection():
    current_route = "contextual"
    return TreeNode(
        "contextual",
        "malankara",
        children=[
            TreeNode(
                "benedictionSongs",
                current_route,
                f"{current_route}/benedictionSongs.json",
            ),
            TreeNode(
                "intercessionToMary",
                current_route,
                f"{current_route}/intercessionToMary.json",
            ),
            TreeNode(
                "beforeFood",
                current_route,
                f"{current_route}/beforeFood.json",
            ),
            TreeNode(
                "afterFood",
                current_route,
                f"{current_route}/afterFood.json",
            ),
            TreeNode(
                "forSick",
                current_route,
                f"{current_route}/forSick.json",
            ),
        ],
    )


base_tree = TreeNode(
    "malankara",
    None,
    children=[
        common_prayers_section(),
        daily_prayers_section(),
        sacraments_section(),
        feastsSection(),
        contextualSection(),
    ],
)


def get_available_languages(base_filename: str) -> list[str]:
    """Return all languages for which a file exists in JSON/prayers/{lang}/."""
    found = []
    for lang in LANGUAGES:
        file_path = Path("JSON", "prayers", lang, base_filename)
        if Path.is_file(file_path):
            found.append(lang)
            os.path.getmtime(file_path)
    return found


def enrich_node(base_dir: str, node: TreeNode) -> TreeNode:
    """Recursively enrich the node with languages field."""
    if node.filename is None:
        languages = set()
        for child in node.children:
            enrich_node(base_dir, child)
            languages = languages.union(set(child.languages))
        node.languages = list(languages)
    elif node.filename is not None:
        node.languages = get_available_languages(node.filename)
    print(f"Enriched node: {node.route} with languages: {node.languages}")
    return node


def save_tree_to_file(tree: TreeNode, file_path: str | Path):
    """Saves the tree to a JSON file."""
    path = Path(file_path)
    with open(path.resolve().__str__(), "w", encoding="utf-8") as f:
        json.dump(tree.to_dict(), f, ensure_ascii=False, indent=4)
    print(f"Saved tree to {file_path}")


if __name__ == "__main__":
    print(base_tree.to_dict())
    print("\n\n")
    data = enrich_node("D:/Liturgica/Documents", base_tree)
    save_tree_to_file(data, "D:/Liturgica/Documents/prayers_tree.json")
