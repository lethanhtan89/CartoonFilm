package tan.le.cartoonfilm.base;

import android.content.Context;

import com.squareup.haha.perflib.Main;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final MainApplication mainApplication;

    public AppModule(MainApplication mainApplication) {
        this.mainApplication = mainApplication;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mainApplication;
    }

}
