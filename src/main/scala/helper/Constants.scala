package helper

object Constants {
	val START_ID = 5000000000L
	val END_ID = 5999999999L
	val FEEDS_NUMBER = 100
	val STEAM_KEY = "&key=0D1D08A26851831B5C107E9DDB80EC0E"
	val STEAM_API = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?match_id="
	val ATTRIBUTES = Array(
		"gold_per_min", "level", "leaver_status", "xp_per_min", "gold_spent",
		"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing"
	)

	val MAIN_ROUTE = "C:/Users/Labinot/Desktop/main_route/"

	val FETCHED_STEAM_DATA = "fetched_steam_data"
	val CLASSIFIED_MODEL = "classified_model"
	val CLUSTERED_MODEL = "clustered_model"
	val RAW_KAGGLE_DATA = "raw_kaggle_data"
	val KAGGLE_DATA = "kaggle_data"
}