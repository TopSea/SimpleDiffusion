package top.topsea.simplediffusion.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tencent.mmkv.MMKV
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import top.topsea.simplediffusion.api.GenImgApi
import top.topsea.simplediffusion.api.NormalApi
import top.topsea.simplediffusion.api.PromptApi
import top.topsea.simplediffusion.api.SocketClient
import top.topsea.simplediffusion.api.impl.GenImgApiImp
import top.topsea.simplediffusion.api.impl.NormalApiImp
import top.topsea.simplediffusion.api.impl.PromptApiImp
import top.topsea.simplediffusion.data.DiffusionDatabase
import top.topsea.simplediffusion.data.param.CNParamDao
import top.topsea.simplediffusion.data.param.ImageDataDao
import top.topsea.simplediffusion.data.param.ImgParamDao
import top.topsea.simplediffusion.data.param.TaskParamDao
import top.topsea.simplediffusion.data.param.TxtParamDao
import top.topsea.simplediffusion.data.param.UserPromptDao
import top.topsea.simplediffusion.util.TextUtil
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMMKV(): MMKV = MMKV.defaultMMKV()

    @Singleton
    @Provides
    fun provideLogInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor { message -> //打印retrofit日志
            TextUtil.topsea("HTTP_LOG: $message", level = Log.WARN)
        }
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        return loggingInterceptor
    }

    @Singleton
    @Provides
    fun provideInterceptor(kv: MMKV): Interceptor {
        // OkHttpClient的拦截器，用于拦截retrofit里header的配置
        val interceptor = Interceptor { chain ->
            val request: Request = chain.request()
            val builder: Request.Builder = request.newBuilder()

            val serverIP = kv.decodeString("Server_IP", "http://192.168.0.107:7860")!!

            val url = serverIP.toHttpUrl()
            val trueUrl = url.newBuilder()
                .scheme(url.scheme)
                .host(url.host)
                .port(url.port)
                .encodedPath(request.url.encodedPath)
                .build()

            val newRequest: Request = builder.url(trueUrl).build()
            return@Interceptor chain.proceed(newRequest)
        }

        return interceptor
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideSocketClient(kv: MMKV): SocketClient {
        val serverIP = kv.decodeString("Server_IP", "http://192.168.0.107:7860")!!
//        val socketAddress = if (serverIP.startsWith("http://")) {
//            serverIP.split("http://")[1]
//        } else if (serverIP.startsWith("https://")) {
//            serverIP.split("https://")[1]
//        } else {
//            ""
//        }
        val socketAddress = "240e:380:69e0:ac00:aec0:c9ff:a8f6:50de"

        return SocketClient(socketAddress) { address, exception ->
            TextUtil.topsea("address: $address, exception: ${exception?.message}", Log.ERROR)
        }.apply {
            startClient({
                TextUtil.topsea("Socket address: $it", Log.ERROR)
            }){
                TextUtil.topsea("Socket exception: ${it?.message}", Log.ERROR)
            }
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl("http://a.b/c:1/")
            .build()
    }

    @Provides
    @Singleton
    fun provideNewbieApi(retrofit: Retrofit): GenImgApi = retrofit.create(GenImgApi::class.java)

    @Provides
    @Singleton
    fun provideNewbieApiImp(genImgApi: GenImgApi): GenImgApiImp {
        return GenImgApiImp(genImgApi)
    }

    @Provides
    @Singleton
    fun provideNormalApi(retrofit: Retrofit): NormalApi = retrofit.create(NormalApi::class.java)

    @Provides
    @Singleton
    fun provideNormalApiImp(normalApi: NormalApi): NormalApiImp {
        return NormalApiImp(normalApi)
    }

    @Provides
    @Singleton
    fun providePromptApi(retrofit: Retrofit): PromptApi = retrofit.create(PromptApi::class.java)

    @Provides
    @Singleton
    fun providePromptApiImp(promptApi: PromptApi): PromptApiImp {
        return PromptApiImp(promptApi)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): DiffusionDatabase {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE TxtParam" +
                        " ADD COLUMN priority_order INTEGER NOT NULL DEFAULT 0")

                database.execSQL("ALTER TABLE ImgParam" +
                        " ADD COLUMN priority_order INTEGER NOT NULL DEFAULT 0")

                database.execSQL("ALTER TABLE CNParam" +
                        " ADD COLUMN priority_order INTEGER NOT NULL DEFAULT 0")
            }
        }
        return Room.databaseBuilder(
            appContext,
            DiffusionDatabase::class.java,
            "SimpleDiffusion.db"
        )
            .addMigrations(MIGRATION_1_2)
//            .createFromAsset("SimpleDiffusion.db")
//            .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideImgDataDao(diffusionDatabase: DiffusionDatabase): ImageDataDao {
        return diffusionDatabase.imageDataDao()
    }

    @Provides
    fun provideUserPromptDao(diffusionDatabase: DiffusionDatabase): UserPromptDao {
        return diffusionDatabase.userPromptDao()
    }

    @Provides
    fun provideCNParamDao(diffusionDatabase: DiffusionDatabase): CNParamDao {
        return diffusionDatabase.cnParamDao()
    }

    @Provides
    fun provideImgParamDao(diffusionDatabase: DiffusionDatabase): ImgParamDao {
        return diffusionDatabase.imgParamDao()
    }

    @Provides
    fun provideTxtParamDao(diffusionDatabase: DiffusionDatabase): TxtParamDao {
        return diffusionDatabase.txtParamDao()
    }

    @Provides
    fun provideTaskParamDao(diffusionDatabase: DiffusionDatabase): TaskParamDao {
        return diffusionDatabase.taskParamDao()
    }

}