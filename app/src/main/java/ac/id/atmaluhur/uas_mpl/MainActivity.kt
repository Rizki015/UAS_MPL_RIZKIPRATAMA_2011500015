package ac.id.atmaluhur.uas_mpl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.*
import org.json.JSONException
import org.json.JSONObject
import java.net.NetworkInterface
import java.net.SocketException


class MainActivity : AppCompatActivity() {
    private lateinit var url : String
    private lateinit var sr : StringRequest
    private lateinit var rq : RequestQueue

    private lateinit var rvPerpustakaan : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btntambah =findViewById<Button>(R.id.btnEntriData)

        title = "Perpustakaan 015-039"

        rvPerpustakaan = findViewById(R.id.rvPerpustakaan)

        rvPerpustakaan.setHasFixedSize(true)
        rvPerpustakaan.layoutManager = LinearLayoutManager(this@MainActivity)

        btntambah.setOnClickListener{
            val i = Intent(this@MainActivity,EntriPerpustakaan ::class.java)
            startActivity(i)
        }
    }
    private fun getDefaultGateaway(): String? {
        var defaultGateaway: String? = null
        try {
            val enumNetworkInterface = NetworkInterface.getNetworkInterfaces()
            while (enumNetworkInterface.hasMoreElements()) {
                val networkInterface = enumNetworkInterface.nextElement()
                val enumInetAddress = networkInterface.inetAddresses
                while (enumInetAddress.hasMoreElements()) {
                    val inetAddress = enumInetAddress.nextElement()
                    if (inetAddress.isSiteLocalAddress) defaultGateaway = inetAddress.hostAddress
                }
            }
        } catch (_: SocketException) {
            defaultGateaway = null
        }
        return defaultGateaway
    }
    override fun onStart() {
    super.onStart()
    val ipSebelumnya = ip
    if(getDefaultGateaway() != null) {
        try {
            for (i in 0..255) {
                val kepalaIp =
                    getDefaultGateaway()?.substring(0, getDefaultGateaway()?.lastIndexOf(".") ?: -1)
                val ipTemp = "$kepalaIp.$i"
                url = "https://$ipTemp/UasMPL/koneksi.php"
                sr = StringRequest(Request.Method.GET,url,{
                    if (it.isNotEmpty()) {
                        ip = ipTemp
                        if (ip != ipSebelumnya) {
                            Toast.makeText(
                                this@MainActivity,
                                "Terhubung ke $ip",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    }, null)
                    rq = Volley.newRequestQueue(this@MainActivity)
                    rq.add(sr)
                }

            } catch (_: Exception) {
                ip = "192.168.43.99"
            }
        } else ip = "192.168.43.99"
        tampilData()

        }
    override fun onResume() {
        super.onResume()
        tampilData()
    }
    private fun tampilData () {
        val listPerpustakaan = arrayListOf<Perpustakaan>()
        val adapter = AdapterPerpustakaan(listPerpustakaan, this@MainActivity)

        url = "https://$ip/UasMPL/tampil.php"
        sr = StringRequest(Request.Method.GET, url,{
            try {
                val obj = JSONObject(it)
                val array = obj.getJSONArray("data")
                for (i in 0 until array.length()) {
                    val ob = array.getJSONObject(i)
                    with(ob) {
                        listPerpustakaan.add(Perpustakaan(
                            getString("Isbn"),
                            getString("Judul_Buku"),
                            getString("Pengarang"),
                            getString("Penerbit"),
                            getString("ThnTerbit"),
                            getString("TempatTerbit"),
                            getString("Cetakanke"),
                            getString("Jumlah_Halaman"),
                            getInt("Klasifikasi")
                            ))
                    }
                }
                rvPerpustakaan.adapter = adapter
                    } catch (_: JSONException) {
                        Toast.makeText(
                            this@MainActivity, "Tidak ada data...", Toast.LENGTH_LONG).show()
            }
        }, null)
        rq = Volley.newRequestQueue(this@MainActivity)
        rq.add(sr)
    }
}

