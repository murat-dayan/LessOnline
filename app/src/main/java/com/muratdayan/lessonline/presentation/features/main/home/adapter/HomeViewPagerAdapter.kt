package com.muratdayan.lessonline.presentation.features.main.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.muratdayan.lessonline.presentation.features.main.home.all_posts.AllPostsFragment
import com.muratdayan.lessonline.presentation.features.main.home.following_posts.FollowingPostsFragment

class HomeViewPagerAdapter(
    fragment: Fragment,
    val onPhotoClick:(String)->Unit
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> AllPostsFragment(){onPhotoClick(it)}
            1-> FollowingPostsFragment(){onPhotoClick(it)}
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

}