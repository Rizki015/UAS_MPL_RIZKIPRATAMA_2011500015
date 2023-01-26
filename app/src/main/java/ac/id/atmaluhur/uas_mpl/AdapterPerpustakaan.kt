package ac.id.atmaluhur.uas_mpl

import android.app.AlertDialog
import android.view.*
import android.widget.*
import android.content.*
import android.graphics.Color
import android.text.TextUtils
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso


class AdapterPerpustakaan(
    val listPerpustakaan: ArrayList<Perpustakaan>, val context: Context):
    RecyclerView.Adapter<AdapterPerpustakaan.ViewHolder>() {

    override fun onCreateViewHolder(
        parent:ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_layout_perpustakaan, parent,
        false)
    return ViewHolder(view)
    }

override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val la = listPerpustakaan[position]
    val ISBN = la.Isbn
    val judul = la.judulbuku
    val pengarang = la.pengarang
    val penerbit = la.penerbit
    val tahunterbit = la.tahunterbit
    val tempatterbit = la.tempatterbit
    val cetakanke = la.cetakanke
    val jumlahhalaman = la.jmlhal
    val klasifikasi = la.klasifikasi
    val warnaklasifikasi = Color.parseColor(when(pengarang) {
        "Umum" -> "#FF0000"
        "Filsafat" -> "#FF7F00"
        "Agama" -> "#FFFF00"
        "Sosial" -> "#00FF00"
        "Bahasa" -> "#0000FF"
        "Ilmu Murni/Sains" -> "#4B0082"
        "Teknologi" -> "#8F00FF"
        "Seni" -> "#F56FA1"
        "Satra" -> "#FBEC5D"
        "Geografi/Sejarah" -> "#228B22"
        else -> "800000"
        })
    val databuku = """
        ISBN: $ISBN,
        Judul Buku: $judul,
        Pengarang: $pengarang,
        penerbit: $penerbit,
        Tahun Terbit: $tahunterbit,
        Tempat Terbit $tempatterbit,
        Cetakan Ke: $cetakanke,
        Jumlah Halaman: $jumlahhalaman,
        klasifikasi: $klasifikasi,
    """.trimIndent()
    val baseUrl = "http://$ip/UasMPL/foto/"
    with(holder) {
        CvPerpustakaan.setCardBackgroundColor(warnaklasifikasi)
        tvJudul.text = judul
        tvJudul.setTextColor(if ( pengarang != "Umum") Color.WHITE else Color.BLACK)
        tvPengarang.text = "$judul $klasifikasi"
        tvPengarang.setTextColor(if ( pengarang != "Umum") Color.WHITE else Color.BLACK)
        Picasso.get().load("$baseUrl$ISBN.jpeg").fit().into(imgFoto)
        itemView.setOnClickListener {
            val alb = AlertDialog.Builder(context)
            with(alb) {
                setCancelable(false)
                setTitle("Data Perpustakaan")
                setMessage(databuku)
                setPositiveButton("Ubah") { _, _ ->
                    val i = Intent(context, EntriPerpustakaan::class.java)
                    with(i) {
                        putExtra("ISBN", ISBN)
                        putExtra("Judul Buku", judul)
                        putExtra("pengarang", pengarang)
                        putExtra("penerbit", penerbit)
                        putExtra("tempatterbit", tempatterbit)
                        putExtra("tahunterbit", tahunterbit)
                        putExtra("cetakanke", cetakanke)
                        putExtra("jumlahhalaman", jumlahhalaman)
                        putExtra("klasifikasi", klasifikasi)
                    }
                    context.startActivity(i)
                }
            setNegativeButton("Hapus") { _, _ ->
                val url = "https:/$ip/UasMPL/hapus.php?ISBN=$ISBN"
                val sr = StringRequest(Request.Method.GET, url, {
                    Toast.makeText(
                        context,
                        "Data Perpustakaan [$ISBN] $it Dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (it == "Berhasil") {
                        listPerpustakaan.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }, null)
                val rq = Volley.newRequestQueue(context)
                rq.add(sr)
            }
        setNeutralButton("Tutup", null)
        create().show()


            }
        }
    }
}

override fun getItemCount() = listPerpustakaan.size

private fun String.thnString(): String {
    val tahunterbit = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    val tanggal = TextUtils.split(this, "-")
    val thn = tanggal[0]
    val bln = tahunterbit[tanggal[1].toInt() - 1]
    val tgl = tanggal[2].toInt()
    return "$tgl $bln $thn"
  }
    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        val CvPerpustakaan = itemView.findViewById<CardView>(R.id.cvPerpustakaan)
        val imgFoto = itemView.findViewById<ImageView>(R.id.imageView)
        val tvJudul = itemView.findViewById<TextView>(R.id.tv2judulbukulv)
        val tvPengarang = itemView.findViewById<TextView>(R.id.tv2pengarangLp)
    }

}



