package com.mathewsachin.fategrandautomata.scripts.models

import com.mathewsachin.libautomata.Location

class ServantTarget private constructor(
    val clickLocation: Location,
    val autoSkillCode: Char
) {
    companion object {
        val list = listOf(
            ServantTarget(
                Location(600, 880),
                '1'
            ),
            ServantTarget(
                Location(1180, 880),
                '2'
            ),
            ServantTarget(
                Location(1800, 880),
                '3'
            ),

            // Emiya
            ServantTarget(
                Location(990, 880),
                '7'
            ),
            ServantTarget(
                Location(1610, 880),
                '8'
            )
        )
    }
}