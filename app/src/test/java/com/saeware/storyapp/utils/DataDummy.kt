package com.saeware.storyapp.utils

import com.saeware.storyapp.data.local.entity.Story
import com.saeware.storyapp.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.saeware.storyapp.data.remote.response.Story as StoryResponse

object DataDummy {
    const val DUMMY_TOKEN = "auth_token"
    const val DUMMY_NAME = "user dummy"
    const val DUMMY_EMAIL = "user@mail.com"
    const val DUMMY_PASSWORD = "password"

    fun generateDummyLoginResponse(): LoginResponse {
        val loginResult = LoginResult(
            userId = "user-CkjdvZNUx7WHn7_M",
            name = "Herdi Herdianurdin",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUNramR2Wk5VeDdXSG43X00iLCJpYXQiOjE2NjgxMzY1NzV9.H2VaLjtAy--zp6i2DSNeyUxH3RiEv90T1ZJkygyVXj8"
        )

        return LoginResponse(
            loginResult = loginResult,
            error =  false,
            message = "success"
        )
    }

    fun generateDummyRegisterResponse() = RegisterResponse(
        error = false,
        message = "success"
    )

    fun generateDummyStories(): List<Story> {
        val stories = arrayListOf<Story>()

        for (i in 0..9) {
            val story = Story(
                id = "story-EGlZxs22s1avi8x3",
                name = "reviewer123",
                description = "dddddddddddddd",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1668136537864_xHpaxbui.jpg",
                createdAt = "2022-11-11T03:15:37.866Z",
                lat = 37.30421,
                lon = -122.0702483
            )

            stories.add(story)
        }

        return stories
    }

    fun generateDummyStoriesResponse(): StoriesResponse {
        val error = false
        val message = "Stories fetched successfully"
        val stories = mutableListOf<StoryResponse>()

        for (i in 0..9) {
            val story = StoryResponse(
                id = "story-EGlZxs22s1avi8x3",
                name = "reviewer123",
                description = "dddddddddddddd",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1668136537864_xHpaxbui.jpg",
                createdAt = "2022-11-11T03:15:37.866Z",
                lat = 37.30421,
                lon = -122.0702483
            )

            stories.add(story)
        }

        return StoriesResponse(error, message, stories)
    }

    fun generateDummyMultipartFile() =
        MultipartBody.Part.create("file".toRequestBody())

    fun generateDummyRequestBody() = "text".toRequestBody()

    fun generateDummyPostResponse() =
        PostResponse(error = false, message = "success")
}