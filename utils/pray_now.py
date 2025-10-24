from datetime import datetime, timedelta
import calendar


def pray_now(now: datetime | None = None):
    prayer_list = list()
    if now is None:
        now = datetime.now()
    hour = now.hour
    current_day = now.weekday()
    if hour >= 18:
        current_day += 1
    if current_day > 6:
        current_day = 0

    def decide_time(option: str):
        if hour >= 18 and hour < 22:
            prayer_list.append(f"{option}_sandhya")
        if hour >= 18 and hour < 24:
            prayer_list.append(f"{option}_soothaara")
        if hour >= 20 or hour <= 6:
            prayer_list.append(f"{option}_rathri")
        if hour >= 5 and hour < 12:
            prayer_list.append(f"{option}_prabaatham")
        if hour >= 5 and hour <= 17:
            prayer_list.append(f"{option}_moonaam")
            prayer_list.append(f"{option}_aaraam")
        if hour >= 11 and hour < 18:
            prayer_list.append(f"{option}_onbathaam")
        return prayer_list

    if current_day != 6:
        decide_time(f"sheema_{calendar.day_name[current_day].lower()}")
    if (current_day == 6 or current_day == 0) and (hour >= 11 and hour <= 13):
        prayer_list.append("ring")
        prayer_list.append("crown")
    decide_time("sleeba")
    return prayer_list


if __name__ == "__main__":

    # def test_time(i):
    #     current_time = datetime(2025, 5, 19, i, 0, 0, 0)
    #     prayer_list = pray_now(current_time)
    #     print("Current time: ", current_time)
    #     print("Prayer list: ", prayer_list)

    # # for i in range(18, 24):
    # #     test_time(i)
    # # for i in range(0, 18):
    # #     test_time(i)
    # for i in range(0, 24):
    #     test_time(i)
    current_time = datetime.now()
    prayer_list = pray_now(current_time)
    print("Current time: ", current_time)
    print("Prayer list: ", prayer_list)
