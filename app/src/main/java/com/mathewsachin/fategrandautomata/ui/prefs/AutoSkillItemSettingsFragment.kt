package com.mathewsachin.fategrandautomata.ui.prefs

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.*
import com.google.gson.Gson
import com.mathewsachin.fategrandautomata.R
import com.mathewsachin.fategrandautomata.StorageDirs
import com.mathewsachin.fategrandautomata.prefs.core.PrefsCore
import com.mathewsachin.fategrandautomata.scripts.enums.SupportSelectionModeEnum
import com.mathewsachin.fategrandautomata.scripts.prefs.IAutoSkillPreferences
import com.mathewsachin.fategrandautomata.scripts.prefs.IPreferences
import com.mathewsachin.fategrandautomata.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.error
import java.util.*
import javax.inject.Inject
import com.mathewsachin.fategrandautomata.prefs.R.string as prefKeys

@AndroidEntryPoint
class AutoSkillItemSettingsFragment : PreferenceFragmentCompat() {
    @Inject
    lateinit var preferences: IPreferences

    @Inject
    lateinit var storageDirs: StorageDirs

    @Inject
    lateinit var prefsCore: PrefsCore

    val args: AutoSkillItemSettingsFragmentArgs by navArgs()

    val autoSkillExport = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        if (uri != null) {
            try {
                val values = preferences.forAutoSkillConfig(args.key).export()
                val gson = Gson()
                val json = gson.toJson(values)

                requireContext().contentResolver.openOutputStream(uri)?.use { outStream ->
                    outStream.writer().use { it.write(json) }
                }
            } catch (e: Exception) {
                Timber.error(e) { "Failed to export" }

                val msg = getString(R.string.auto_skill_item_export_failed)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var autoSkillPrefs: IAutoSkillPreferences

    private fun findFriendNamesList(): MultiSelectListPreference? =
        findPreference(getString(prefKeys.pref_support_friend_names))

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = args.key
        autoSkillPrefs = preferences.forAutoSkillConfig(args.key)

        setHasOptionsMenu(true)

        setPreferencesFromResource(R.xml.autoskill_item_preferences, rootKey)

        findFriendNamesList()?.summaryProvider = SupportMultiSelectListSummaryProvider()

        findPreference<EditTextPreference>(getString(R.string.pref_autoskill_notes))?.makeMultiLine()

        findPreference<Preference>(getString(prefKeys.pref_card_priority))?.let {
            it.setOnPreferenceClickListener {
                val action = AutoSkillItemSettingsFragmentDirections
                    .actionAutoSkillItemSettingsFragmentToCardPriorityFragment(args.key)

                nav(action)

                true
            }
        }

        findPreference<EditTextPreference>(getString(prefKeys.pref_autoskill_cmd))?.let {
            it.setOnPreferenceClickListener {
                if (!prefsCore.showTextBoxForAutoSkillCmd.get()) {
                    val action = AutoSkillItemSettingsFragmentDirections
                        .actionAutoSkillItemSettingsFragmentToAutoSkillMakerActivity(args.key)

                    nav(action)
                }

                true
            }
        }

        findPreference<Preference>(getString(R.string.pref_nav_preferred_support))?.let {
            it.setOnPreferenceClickListener {
                val action = AutoSkillItemSettingsFragmentDirections
                    .actionAutoSkillItemSettingsFragmentToPreferredSupportSettingsFragment(args.key)

                nav(action)

                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vm: AutoSkillItemViewModel by viewModels()

        findPreference<Preference>(getString(prefKeys.pref_card_priority))?.let {
            vm.cardPriority.observe(viewLifecycleOwner) { priority ->
                it.summary = priority
            }
        }

        findPreference<EditTextPreference>(getString(R.string.pref_autoskill_cmd))?.let {
            vm.skillCommand.observe(viewLifecycleOwner) { cmd ->
                it.text = cmd
            }
        }

        val navPreferred = findPreference<Preference>(getString(R.string.pref_nav_preferred_support))
        val friendNames = findFriendNamesList()
        val fallback = findPreference<Preference>(getString(prefKeys.pref_support_fallback))

        vm.preferredMessage.observe(viewLifecycleOwner) { msg ->
            navPreferred?.summary = msg
        }

        vm.supportSelectionMode.observe(viewLifecycleOwner) {
            val preferred = it == SupportSelectionModeEnum.Preferred
            val friend = it == SupportSelectionModeEnum.Friend

            friendNames?.isVisible = friend
            fallback?.isVisible = preferred || friend
            navPreferred?.isVisible = preferred
        }
    }

    override fun onResume() {
        super.onResume()

        if (storageDirs.shouldExtractSupportImages) {
            performSupportImageExtraction()
        } else populateFriendNames()
    }

    private fun populateFriendNames() {
        findFriendNamesList()?.apply {
            populateFriendOrCe(storageDirs.supportFriendFolder)
        }
    }

    private fun performSupportImageExtraction() {
        lifecycleScope.launch {
            val msg = try {
                SupportImageExtractor(requireContext(), storageDirs).extract()
                populateFriendNames()

                getString(R.string.support_imgs_extracted)
            } catch (e: Exception) {
                getString(R.string.support_imgs_extract_failed).also { msg ->
                    Timber.error(e) { msg }
                }
            }

            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.autoskill_item_menu, menu)
        inflater.inflate(R.menu.support_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_support_extract_defaults -> {
                performSupportImageExtraction()
                true
            }
            R.id.action_auto_skill_delete -> {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.auto_skill_item_delete_confirm_message)
                    .setTitle(R.string.auto_skill_item_delete_confirm_title)
                    .setPositiveButton(R.string.auto_skill_item_delete_confirm_ok) { _, _ -> deleteItem(args.key) }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }
            R.id.action_auto_skill_export -> {
                autoSkillExport.launch("${autoSkillPrefs.name}.fga")
                true
            }
            R.id.action_auto_skill_copy -> {
                val guid = UUID.randomUUID().toString()
                preferences.addAutoSkillConfig(guid)
                val newConfig = preferences.forAutoSkillConfig(guid)

                val map = autoSkillPrefs.export()
                newConfig.import(map)
                newConfig.name = getString(R.string.auto_skill_item_copy_name, newConfig.name)

                val action = AutoSkillItemSettingsFragmentDirections
                    .actionAutoSkillItemSettingsFragmentSelf(guid)

                nav(action)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteItem(AutoSkillItemKey: String) {
        preferences.removeAutoSkillConfig(AutoSkillItemKey)

        findNavController().popBackStack()
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        fun prepare(dialogFragment: PreferenceDialogFragmentCompat) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(parentFragmentManager, null)
        }

        when (preference.key) {
            getString(R.string.pref_support_friend_names) -> {
                ClearMultiSelectListPreferenceDialog().apply {
                    setKey(preference.key)
                    prepare(this)
                }
            }
            getString(R.string.pref_autoskill_cmd) -> {
                if (prefsCore.showTextBoxForAutoSkillCmd.get()) {
                    SkillCmdPreferenceDialogFragment().apply {
                        autoSkillKey = args.key
                        prepare(this)
                    }
                }
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }
}