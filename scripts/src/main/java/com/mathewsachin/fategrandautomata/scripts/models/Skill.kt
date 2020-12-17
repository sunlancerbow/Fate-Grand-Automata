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
                Servant(Location(0, 1100), 'a'),
                Servant(Location(180, 1100), 'b'),
                Servant(Location(360, 1100), 'c'),

                Servant(Location(640, 1100), 'd'),
                Servant(Location(820, 1100), 'e'),
                Servant(Location(990, 1100), 'f'),

                Servant(Location(1270, 1100), 'g'),
                Servant(Location(1450, 1100), 'h'),
                Servant(Location(1630, 1100), 'i')
            )
        }
    }

    class Master private constructor(
        clickLocation: Location,
        autoSkillCode: Char
    ) : Skill(clickLocation, autoSkillCode) {
        companion object {
            val list = listOf(
                Master(Location(1950, 640), 'j'),
                Master(Location(2120, 640), 'k'),
                Master(Location(2300, 640), 'l')
            )
        }
    }
}