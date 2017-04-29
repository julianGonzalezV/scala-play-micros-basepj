package filters

import javax.inject.Inject

import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter


/**
  * Created by juligove on 2017/02/20.
  */
class MyFilter  @Inject()(corsFilter: CORSFilter)
  extends DefaultHttpFilters(corsFilter) {

}
