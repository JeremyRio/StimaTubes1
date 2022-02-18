# StimaTubes1
 
## Daftar Isi
* [Deskripsi](#deskripsi)
* [Requirements](#requirements)
* [Penggunaan](#penggunaan)
* [Penulis](#penulis)

## Deskripsi
Membuat program bot berdasarkan algoritma greedy untuk memenangkan permainan Overdrive. Algoritma greedy memprioritaskan strategi untuk menyerang lawan sambil menghindari rintangan di jalan. Prioritas Algoritma dimulai dari yang paling penting adalah sebagai berikut :

1. Menghindari rintangan
2. Menyerang mobil lawan
3. Mencapai kecepatan maksimum

Ketika menghindari rintangan, program akan menentukan jalur terbaik berdasarkan jumlah rintangan dan PowerUp yang ada pada baris tersebut sehingga pemain memiliki lebih banyak PowerUp yang dapat digunakan untuk menyerang musuh.

## Requirements
- Windows **(Recommended)**
- [Java](https://www.java.com/en/download/)
- [IntelliJ](https://www.jetbrains.com/idea/) **(Recommended)**
- [Maven](https://maven.apache.org/)

### Langkah Menjalankan Program
### 1. Build Program Menggunakan Maven (**Windows**)
Build bertujuan untuk membuat file .jar yang akan digunakan game engine untuk menjalankan permainan. Ada 2 metode yang dapat digunakan untuk build program.
#### A. Menggunakan IntelliJ (**Recommended**)
1. Melakukan instalasi IntelliJ (aplikasi dapat ditemukan dari https://www.jetbrains.com/idea/ )
2. Buka folder "Java" tempat program bot ditemukan.
![image](https://user-images.githubusercontent.com/40627156/154693437-92be8bc4-a363-4e7e-b6aa-5b4a7ce8e75b.png)

3. Menekan tombol Maven di kanan atas layar dan melakukan build dengan menekan tombol install
![image](https://user-images.githubusercontent.com/40627156/154693758-b0263be8-be29-480c-a8c0-4789acc03b23.png)

#### B. Menggunakan Maven Di Terminal Komputer
1. Melakukan instalasi Maven (file instalasi dapat ditemukan di https://maven.apache.org/download.cgi )
2. Buka folder "Java" tempat program bot ditemukan
3. Memasukkan perintah

```
mvn clean install
```

### 2. Mengatur Permainan
