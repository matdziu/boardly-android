package com.boardly.discover.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.location.UserLocation
import com.boardly.discover.models.Place
import io.reactivex.Observable

class DiscoverServiceImpl : DiscoverService, BaseServiceImpl() {

    override fun fetchPlacesList(userLocation: UserLocation, radius: Double): Observable<List<Place>> {
        return Observable.just(listOf(Place(
                "1",
                "Domówka",
                "Najlepsza knajpka w mieście.",
                "https://www.where2b.org/media/cache/thumb_photo/uploads/album/c/51dc20f75a13c/51df2d550ef1f.jpeg",
                "Miodowa 28",
                50.0,
                19.0,
                "662644344",
                "http://www.domowkacafe.pl/"
        ),
                Place(
                        "2",
                        "Domówka",
                        "Najlepsza knajpka w mieście.",
                        "https://www.where2b.org/media/cache/thumb_photo/uploads/album/c/51dc20f75a13c/51df2d550ef1f.jpeg",
                        "Miodowa 28",
                        50.0,
                        19.0,
                        "662644344",
                        "http://www.domowkacafe.pl/"
                ),
                Place(
                        "3",
                        "Domówka",
                        "Najlepsza knajpka w mieście.",
                        "https://www.where2b.org/media/cache/thumb_photo/uploads/album/c/51dc20f75a13c/51df2d550ef1f.jpeg",
                        "Miodowa 28",
                        50.0,
                        19.0,
                        "662644344",
                        "http://www.domowkacafe.pl/"
                )))
    }
}