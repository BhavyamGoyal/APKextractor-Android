package com.impracticallabs.www.apkextractor

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log.i
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnContextItemClickListener {

    private lateinit var progressBar: ProgressBar
    private val apkList = ArrayList<ApkData>()
    private lateinit var contextItemPackageName: String

    private lateinit var mAdapter: ApkCardAdapter
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utilities.checkPermission(this)
        mLinearLayoutManager = LinearLayoutManager(this)
        mAdapter = ApkCardAdapter(apkList, this)
        mRecyclerView = findViewById(R.id.apkRecyclerView)
        progressBar=findViewById(R.id.loading);
        mRecyclerView.layoutManager = mLinearLayoutManager
        mRecyclerView.adapter = mAdapter


        loadApk()
    }

    private fun loadApk() {
        doAsync {
            val allPackages: List<PackageInfo> = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

            allPackages.forEach {
                val applicationInfo: ApplicationInfo = it.applicationInfo
                i("Logger","backGroundThread");
                val userApk = ApkData(
                    applicationInfo,
                    packageManager.getApplicationLabel(applicationInfo).toString(),
                    it.packageName,
                    it.versionName)
                apkList.add(userApk)
            }

            uiThread {
                mAdapter.notifyDataSetChanged()
                i("Logger","uiThread");
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Utilities.STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Utilities.makeAppDir()
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Permission required to extract apk", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_launch -> {
                try {
                    startActivity(packageManager.getLaunchIntentForPackage(contextItemPackageName))
                } catch (e: Exception) {
                    Snackbar.make(findViewById(android.R.id.content), "Can't open this app", Snackbar.LENGTH_SHORT).show()
                }
            }
            R.id.action_playstore -> {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$contextItemPackageName")))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$contextItemPackageName")))
                }

            }
        }
        return true
    }

    override fun onItemClicked(packageName: String) {
        contextItemPackageName = packageName
    }
}