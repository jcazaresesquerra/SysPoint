package com.app.syspoint.usecases

import com.app.syspoint.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class GetUpdatesUseCase {

    suspend operator fun invoke(): Flow<Resource<Boolean>> = callbackFlow {


    }
}