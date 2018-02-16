package com.matthiasbruns.kleanarchitekture.app.feature.post.list

import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.matthiasbruns.kleanarchitekture.app.KleanApp
import com.matthiasbruns.kleanarchitekture.app.R
import com.matthiasbruns.kleanarchitekture.app.base.controller.PresenterController
import com.matthiasbruns.kleanarchitekture.app.feature.post.list.adapter.PostListAdapter
import com.matthiasbruns.kleanarchitekture.presentation.post.list.PostListView
import com.matthiasbruns.kleanarchitekture.presentation.post.list.presenter.PostListPresenter
import com.matthiasbruns.kleanarchitekture.presentation.post.model.PresentationPostItem
import io.reactivex.Observable
import kotlinx.android.synthetic.main.controller_post_list.view.*
import javax.inject.Inject

class PostListController : PresenterController<PostListPresenter>(), PostListView {

    //region Members

    private val adapter by lazy { PostListAdapter() }

    //endregion

    //region Injects

    @Inject
    override lateinit var presenter: PostListPresenter

    //endregion

    //region PostListView member implementations

    override val onPostClick: Observable<PresentationPostItem>
        get() = adapter.onItemClick

    //endregion

    override fun injectDependencies(context: Context) {
        KleanApp.instance(context)
                ?.postComponent
                ?.plus(PostListModule(this))
                ?.inject(this)
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_post_list, container, false)

        with(view) {
            postListRecyclerView.adapter = adapter
            postListRecyclerView.setHasFixedSize(true)
            postListRecyclerView.layoutManager = LinearLayoutManager(context)
            postListRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        return view
    }

    //region View method implementations

    override fun render(items: List<PresentationPostItem>) {
        adapter.items = items
    }

    override fun showError() {
        activity?.let { context ->
            Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show()
        }

    }

    override fun hideError() {

    }

    //endregion
}