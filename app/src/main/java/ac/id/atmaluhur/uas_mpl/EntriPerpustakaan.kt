package ac.id.atmaluhur.uas_mpl

import androidx.appcompat.app.AppCompatActivity
import android.os.*
import android.widget.*
import com.squareup.picasso.Picasso
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Base64
import android.graphics.*
import androidx.annotation.RequiresApi
import java.util.*
import android.app.DatePickerDialog
import android.content.Intent
import android.text.InputType
import com.android.volley.*
import com.android.volley.toolbox.*
import java.io.ByteArrayOutputStream


class EntriPerpustakaan : AppCompatActivity() {
    private lateinit var url: String
    private lateinit var sr: StringRequest
    private lateinit var rq: RequestQueue

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entri_perpustakaan)

        val modeEdit = intent.hasExtra("Isbn") && intent.hasExtra("jldbuku") &&
                intent.hasExtra("pengarng") && intent.hasExtra("penerbit") &&
                intent.hasExtra("thnterbitr") && intent.hasExtra("tmptterbit") &&
                intent.hasExtra("cetakanke") && intent.hasExtra("jmlhal")
                && intent.hasExtra("klasifikasi")

        title = "${if (!modeEdit) "Tambah" else "Ubah "} Data Perpustakaan"

        val etisbn = findViewById<EditText>(R.id.etIsbn)
        val etjdlbuku = findViewById<EditText>(R.id.etjdlbuku)
        val pengarang = findViewById<EditText>(R.id.etpengarang)
        val penerbit = findViewById<EditText>(R.id.etpenerbit)
        val thnterbit = findViewById<EditText>(R.id.etthnterbit)
        val tmptterbit = findViewById<EditText>(R.id.etTempatterbit)
        val cetakanke = findViewById<EditText>(R.id.etCetakanKe)
        val jmlhhal = findViewById<EditText>(R.id.etjmlhHal)
        val spnklasifikasi = findViewById<Spinner>(R.id.spnKlasifikasi)
        val btncaricover = findViewById<Button>(R.id.btnCariCover)
        val imgfoto = findViewById<ImageView>(R.id.imgfoto)
        val btnsimpan = findViewById<Button>(R.id.btnSimpan)

        val arrKlasifikasi = arrayOf(
            "Umum",
            "Filasfat",
            "Agama",
            "Sosial",
            "Bahasa",
            "Ilmu Murni/Sains",
            "Teknologi",
            "Seni",
            "Sastra",
            "Geografi/Sejarah"
        )
        spnklasifikasi.adapter = ArrayAdapter(
            this@EntriPerpustakaan,
            android.R.layout.simple_spinner_dropdown_item,
            arrKlasifikasi
        )

        if (modeEdit) {
            etisbn.inputType = InputType.TYPE_NULL
            with(intent) {
                etisbn.setText(getStringExtra("isbn"))
                etjdlbuku.setText(getStringExtra("jdl_buku"))
                pengarang.setText(getStringExtra("pemgarang"))
                penerbit.setText(getStringExtra("Penerbit"))
                thnterbit.setText(getStringExtra("thn_terbit"))
                tmptterbit.setText(getStringExtra("tmpt_terbit"))
                cetakanke.setText(getStringExtra("cetakan_ke"))
                jmlhhal.setText("${getStringExtra("jml_hal")}")
                spnklasifikasi.setSelection(arrKlasifikasi.indexOf(getStringExtra("klasifikasi")))
                Picasso.get().load(
                    "http://$ip/php_uas/foto/${getStringExtra("isbn")}.jpeg"
                ).into(imgfoto)
            }
            btnsimpan.text = "Ubah"
        } else {
            etisbn.inputType = InputType.TYPE_CLASS_NUMBER
            btnsimpan.text = "Simpan"
        }
        thnterbit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val kalender = Calendar.getInstance()
                val dpd = DatePickerDialog(
                    this@EntriPerpustakaan,
                    { _, y, m, d ->
                        val bln = String.format("%02d", m + 1)
                        val tgl = String.format("%02d", d)
                        thnterbit.setText("$y-$bln-$tgl")
                    }, kalender[Calendar.YEAR], kalender[Calendar.MONTH],
                    kalender[Calendar.DAY_OF_MONTH]
                )
                dpd.show()
            }
        }

        var foto = " "
        val ambilfoto = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                val source = ImageDecoder.createSource(contentResolver, it)
                foto = imgToString(ImageDecoder.decodeBitmap(source))
                imgfoto.setImageURI(it)
            }
        }

        btncaricover.setOnClickListener { ambilfoto.launch("image/*") }
        btnsimpan.setOnClickListener {
            val i = Intent(this@EntriPerpustakaan,AdapterPerpustakaan ::class.java)
            startActivity(i)
            val isbn = "${etisbn.text}"
            val jdlbuku = "${etjdlbuku.text}"
            val pengarangg = "${pengarang.text}"
            val penerbitt = "${penerbit.text}"
            val thnterbitt = "${thnterbit.text}"
            val tmptterbitt = "${tmptterbit.text}"
            val cetakankee = "${cetakanke.text}"
            val jmlhall = "${jmlhhal.text}"
            val klasifikasii = "${spnklasifikasi.selectedItem}"
            if (btnsimpan.text == "Simpan") {
                url = "http://$ip/php_uas/Simpan.php"
                sr = object : StringRequest(Method.POST, url, {
                    Toast.makeText(
                        this@EntriPerpustakaan,
                        "Data Buku $it disimpan",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (it == "berhasil") finish()
                }, null) {
                    override fun getParams() = mutableMapOf(
                        "isbn" to isbn,
                        "jdlbuku" to jdlbuku,
                        "pengarang" to pengarangg,
                        "penerbit" to penerbitt,
                        "thnterbit" to thnterbitt,
                        "tmptterbit" to tmptterbitt,
                        "cetakanke" to cetakankee,
                        "jmlhal" to jmlhall,
                        "klasifikasi" to klasifikasii,
                        "foto" to foto
                    )
                }
            } else {
                url = "http://$ip/php_uas/Ubah.php"
                sr = object : StringRequest(Method.POST, url, {
                    Toast.makeText(
                        this@EntriPerpustakaan,
                        "Data Buku [$isbn] $it disimpan",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (it == "berhasil") finish()
                }, null) {
                    override fun getParams() = mutableMapOf(
                        "isbn" to isbn,
                        "jdlbuku" to jdlbuku,
                        "pengarang" to pengarangg,
                        "penerbit" to penerbitt,
                        "thnterbit" to thnterbitt,
                        "tmptterbit" to tmptterbitt,
                        "cetakanke" to cetakankee,
                        "jmlhal" to jmlhall,
                        "klasifikasi" to klasifikasii,
                        "foto" to foto
                    )

                }
                rq = Volley.newRequestQueue(this@EntriPerpustakaan)
                rq.add(sr)
            }

            }
        }
    private fun imgToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val  imgbyts = baos.toByteArray()
        return Base64.encodeToString(imgbyts, Base64.DEFAULT)

    }

}







