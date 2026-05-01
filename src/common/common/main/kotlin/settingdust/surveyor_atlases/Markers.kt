package settingdust.surveyor_atlases

import settingdust.surveyor_atlases.util.Identifier

object Markers {
    val BLOCK = surveyor("block")

    val POI_NETHER_PORTAL = minecraft("poi/nether_portal")
    val POI_HOME = minecraft("poi/home")
    val POI_MEETING = minecraft("poi/meeting")
    val POI_LODESTONE = minecraft("poi/lodestone")

    val SURCEYSTONES_BLAYSTONE = Identifier("waystones", "waystone")
    val SURCEYSTONES_FWATSTONES = Identifier("fwaystones", "waystone")

    val DEATH = surveyor("grave")

    private fun minecraft(path: String) = Identifier(path)
    private fun surveyor(path: String) = Identifier("surveyor", path)
}