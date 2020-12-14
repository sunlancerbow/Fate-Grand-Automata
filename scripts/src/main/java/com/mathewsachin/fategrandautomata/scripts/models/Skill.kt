package com.mathewsachin.fategrandautomata.scripts.models

import com.mathewsachin.libautomata.Location

sealed class Skill(
    val clickLocation: Location,
    val autoSkillCode: Char
) {
    class Servant private constructor(
        clickLocation: Location,
        autoSkillCode: Char
    ) : Skill(clickLocation, autoSkillCode) {
        companion object {
            val list = listOf(
                Servant(Location(140, 1155), 'a'),
                Servant(Location(328, 1155), 'b'),
                Servant(Location(514, 1155), 'c'),

                Servant(Location(775, 1155), 'd'),
                Servant(Location(963, 1155), 'e'),
                Servant(Location(1150, 1155), 'f'),

                Servant(Location(1413, 1155), 'g'),
                Servant(Location(1600, 1155), 'h'),
                Servant(Location(1788, 1155), 'i')
            )
        }
    }

    class Master private constructor(
        clickLocation: Location,
        autoSkillCode: Char
    ) : Skill(clickLocation, autoSkillCode) {
        companion object {
            val list = listOf(
                Master(Location(2020, 620), 'j'),
                Master(Location(2200, 620), 'k'),
                Master(Location(2360, 620), 'l')
            )
        }
    }
}