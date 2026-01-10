package com.bagi_bill.bagi_bill.data.repository

import com.bagi_bill.bagi_bill.domain.model.UserProfile

/**
 * Repository interface for user profile operations
 */
interface UserProfileRepository {
    /**
     * Get the user profile
     */
    suspend fun getUserProfile(): UserProfile?

    /**
     * Save or update user profile
     */
    suspend fun saveUserProfile(profile: UserProfile)

    /**
     * Check if user profile exists
     */
    suspend fun hasUserProfile(): Boolean
}

