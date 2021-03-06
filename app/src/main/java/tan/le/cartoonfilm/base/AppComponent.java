package tan.le.cartoonfilm.base;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainApplication application);

    void inject(BaseActivity baseActivity);
}
