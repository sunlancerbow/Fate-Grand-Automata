package com.mathewsachin.fategrandautomata.di.script

import android.widget.Toast
import com.mathewsachin.fategrandautomata.scripts.FgoGameAreaManager
import com.mathewsachin.fategrandautomata.scripts.modules.Game
import com.mathewsachin.fategrandautomata.scripts.modules.Phone
import com.mathewsachin.libautomata.ExitManager
import com.mathewsachin.libautomata.GameAreaManager
import com.mathewsachin.libautomata.IPlatformImpl
import com.mathewsachin.libautomata.dagger.ScriptScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

@Module
@InstallIn(ScriptComponent::class)
class ScriptProvidesModule {
    @ScriptScope
    @Provides
    fun provideExitManager() = ExitManager()

    @ScriptScope
    @Provides
    fun provideGameAreaManager(platformImpl: IPlatformImpl): GameAreaManager {
        Phone.s = android.os.Build.MODEL
        return FgoGameAreaManager(
            platformImpl,
            Game.scriptSize,
            Game.imageSize
        )
    }
}