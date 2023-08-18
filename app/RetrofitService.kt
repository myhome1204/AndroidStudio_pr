interface RetrofitService {
    @POST("SignUp.php")
    fun Register(
        @Field("userId") userId:String,
        @Field("userPassword") userPassword: String,
        @Field("userName") userName:String
    ): Call<Redata>
}