# Create a liturgical calendar for any year based on rules of the Orthodox Church

# Import necessary libraries
from datetime import datetime, timedelta
import calendar
import numpy as np
import json

date = datetime.now()
year = date.year
month = date.month
if month < 10:
    year -= 1
    month = 10
elif month > 10:
    month = 10

json.load = open("calendar_rules.json", "r")

# Nineveh Lent - 71 days before Easter
# Great Lent - 49 days before Easter