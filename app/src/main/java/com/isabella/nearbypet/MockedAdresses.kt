package com.isabella.nearbypet

class MockedAdresses(
    val latitude: Int,
    val longitude: Int
) : LocationProvider{
    override fun getLastLocation() {
        TODO("Not yet implemented")
    }

    override fun getRequestLocation() {
        TODO("Not yet implemented")
    }

}