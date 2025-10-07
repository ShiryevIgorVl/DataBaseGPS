package com.example.KYL.constans

object StringForGPX {
    const val METADATA: String =
        "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\n" +
                "<gpx\n" +
                " version=\"1.0\"\n" +
                " creator=\"OziExplorer Version 3954m - http://www.oziexplorer.com\"\n" +
                " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                " xmlns=\"http://www.topografix.com/GPX/1/0\"\n" +
                " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">\n"

    const val TIME_1: String = "<time>"
    const val TIME_2: String = "</time>\n"
    const val BOUNDS: String = "<bounds minlat=\""
    const val MINILON: String = "\" minlon=\""
    const val MAXLAT: String = "\" maxlat=\""
    const val MAXLON: String = "maxlon=\""
    const val MAXLON_2: String = "\"/>\n"
    const val WPT_LAT: String = "<wpt lat=\""
    const val WPT_LON: String = "\" lon=\""
    const val WPT_LON_2: String = "\">\n"
    const val ELE: String = "<ele>0.000000</ele>\n"
    const val NAME: String = "<name>"
    const val NAME_2: String = "</name>\n"
    const val CMT: String = "<cmt>"
    const val CMT_2: String = "</cmt>\n"
    const val DESC: String = "<desc>"
    const val DESC_2: String = "</desc>\n</wpt>\n"
    const val GPX_2: String = "</gpx>"
}