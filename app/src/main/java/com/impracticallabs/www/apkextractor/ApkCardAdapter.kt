package com.impracticallabs.www.apkextractor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class ApkCardAdapter(var apkList: ArrayList<ApkData>, val context: Context) : RecyclerView.Adapter<ApkCardAdapter.ApkListViewHolder>(){

    //var mItemClickListener: OnContextItemClickListener? = null

    init {
     //   mItemClickListener = context as MainActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApkListViewHolder {
        return ApkListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_apk_item, parent, false), context, apkList)
    }

    override fun onBindViewHolder(holder: ApkListViewHolder, position: Int) {
        holder.mIconImageView.setImageDrawable(context.packageManager.getApplicationIcon(apkList.get(position).appInfo))
        holder.mLabelTextView.text = context.packageManager.getApplicationLabel(apkList.get(position).appInfo).toString()
        holder.mPackageTextView.text = apkList.get(position).packageName
    }

    override fun getItemCount(): Int {
        return apkList.size
    }
    inner class ApkListViewHolder(view: View, context: Context, apkList: ArrayList<ApkData>) : RecyclerView.ViewHolder(view) {

        val mIconImageView: ImageView = view.findViewById(R.id.apk_icon);
        val mLabelTextView: TextView = view.findViewById(R.id.apk_label_tv)
        val mPackageTextView: TextView = view.findViewById(R.id.apk_package_tv)
        private val mExtractBtn: Button = view.findViewById(R.id.extract_button)
        private val mShareBtn: Button = view.findViewById(R.id.share_button)
        private val mUninstallBtn: Button = view.findViewById(R.id.uninstall_button)
        private val mMenuBtn: ImageButton = view.findViewById(R.id.menu_button)

        init {

            (context as Activity).registerForContextMenu(mMenuBtn)

            mExtractBtn.setOnClickListener {
                if (Utilities.checkPermission(context as MainActivity)) {
                    Utilities.extractApk(apkList.get(adapterPosition))
                    val rootView: View = context.window.decorView.findViewById(android.R.id.content)
                    Snackbar.make(rootView, "${apkList.get(adapterPosition).appName} apk extracted successfully", Snackbar.LENGTH_LONG).show()
                }
            }

            mShareBtn.setOnClickListener {
                if (Utilities.checkPermission(context as MainActivity)) {
                    val intent = Utilities.getShareableIntent(apkList.get(adapterPosition))
                    context.startActivity(Intent.createChooser(intent, "Share the apk using"))
                }
            }

            mUninstallBtn.setOnClickListener {
                val uninstallIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
                uninstallIntent.data = Uri.parse("package:" + apkList[adapterPosition].packageName);
                uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                context.startActivity(uninstallIntent)
            }

            mMenuBtn.setOnClickListener {
                mItemClickListener?.onItemClicked(apkList.get(adapterPosition).packageName!!)
                context.openContextMenu(mMenuBtn)
                mMenuBtn.setOnCreateContextMenuListener { menu, _, _ ->
                    context.menuInflater.inflate(R.menu.context_menu, menu)
                }
            }
        }
    }

    interface OnContextItemClickListener {
        fun onItemClicked(packageName: String)
    }
}