import com.egoriku.dependencies.Libs
import com.egoriku.dependencies.Modules.Libraries
import com.egoriku.ext.withLibraries
import com.egoriku.ext.withProjects

plugins {
    id("HappyLibraryPlugin")
    id("com.android.library")
}

withProjects(
        Libraries.core
)

withLibraries(
        Libs.coroutinesAndroid,
        Libs.firebaseFirestoreKtx
)