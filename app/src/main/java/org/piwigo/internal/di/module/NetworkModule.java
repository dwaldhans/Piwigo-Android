/*
 * Piwigo for Android
 * Copyright (C) 2016-2017 Piwigo Team http://piwigo.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.piwigo.internal.di.module;

import com.jakewharton.picasso.OkHttp3Downloader;

import org.piwigo.BuildConfig;
import org.piwigo.accounts.UserManager;
import org.piwigo.internal.di.qualifier.ForPicasso;
import org.piwigo.internal.di.qualifier.ForLogin;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class NetworkModule {


    @Provides @Singleton HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);
        return loggingInterceptor;
    }

    @Provides @Singleton @ForLogin OkHttpClient provideRetrofitOkHttpClient(HttpLoggingInterceptor loggingInterceptor, UserManager userManager) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    Request.Builder builder = chain.request().newBuilder();

                    HttpUrl.Builder urlBuilder = chain.request().url().newBuilder();
                    urlBuilder.addQueryParameter("format", "json");
                    builder.url(urlBuilder.build());

                    return chain.proceed(builder.build());
                })
                .build();
    }

    @Provides @Singleton @ForPicasso OkHttpClient providePicassoOkHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides @Singleton OkHttp3Downloader provideOkHttp3Downloader(@ForPicasso OkHttpClient client) {
        return new OkHttp3Downloader(client);
    }
}
