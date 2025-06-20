PGDMP  4                    }        	   roomiesdb    17.4    17.4 5    \           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            ]           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            ^           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            _           1262    16710 	   roomiesdb    DATABASE     l   CREATE DATABASE roomiesdb WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'pl';
    DROP DATABASE roomiesdb;
                     postgres    false            �            1259    16749    klienci    TABLE     �  CREATE TABLE public.klienci (
    id_klienta integer NOT NULL,
    imie character varying(255) NOT NULL,
    nazwisko character varying(255) NOT NULL,
    ulica character varying(255) NOT NULL,
    numer_budynku character varying(10) NOT NULL,
    pesel character varying(11) NOT NULL,
    czy_aktywny boolean DEFAULT true,
    email character varying(255) NOT NULL,
    telefon character varying(20),
    haslo character varying(255) NOT NULL
);
    DROP TABLE public.klienci;
       public         heap r       postgres    false            �            1259    16748    klienci_id_klienta_seq    SEQUENCE     �   CREATE SEQUENCE public.klienci_id_klienta_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 -   DROP SEQUENCE public.klienci_id_klienta_seq;
       public               postgres    false    222            `           0    0    klienci_id_klienta_seq    SEQUENCE OWNED BY     Q   ALTER SEQUENCE public.klienci_id_klienta_seq OWNED BY public.klienci.id_klienta;
          public               postgres    false    221            �            1259    16740    lokalizacja    TABLE     �   CREATE TABLE public.lokalizacja (
    id_lokalizacji integer NOT NULL,
    nazwa_lokalizacji character varying(255) NOT NULL,
    kraj character varying(255) NOT NULL,
    miasto character varying(255) NOT NULL
);
    DROP TABLE public.lokalizacja;
       public         heap r       postgres    false            �            1259    16739    lokalizacja_id_lokalizacji_seq    SEQUENCE     �   CREATE SEQUENCE public.lokalizacja_id_lokalizacji_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 5   DROP SEQUENCE public.lokalizacja_id_lokalizacji_seq;
       public               postgres    false    220            a           0    0    lokalizacja_id_lokalizacji_seq    SEQUENCE OWNED BY     a   ALTER SEQUENCE public.lokalizacja_id_lokalizacji_seq OWNED BY public.lokalizacja.id_lokalizacji;
          public               postgres    false    219            �            1259    16761 	   platnosci    TABLE     L  CREATE TABLE public.platnosci (
    id_platnosci integer NOT NULL,
    status character varying(20) NOT NULL,
    kwota numeric(10,2) NOT NULL,
    CONSTRAINT platnosci_status_check CHECK (((status)::text = ANY ((ARRAY['OCZEKUJACE'::character varying, 'ANULOWANO'::character varying, 'ZAPŁACONO'::character varying])::text[])))
);
    DROP TABLE public.platnosci;
       public         heap r       postgres    false            �            1259    16760    platnosci_id_platnosci_seq    SEQUENCE     �   CREATE SEQUENCE public.platnosci_id_platnosci_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 1   DROP SEQUENCE public.platnosci_id_platnosci_seq;
       public               postgres    false    224            b           0    0    platnosci_id_platnosci_seq    SEQUENCE OWNED BY     Y   ALTER SEQUENCE public.platnosci_id_platnosci_seq OWNED BY public.platnosci.id_platnosci;
          public               postgres    false    223            �            1259    16769    pokoje    TABLE     (  CREATE TABLE public.pokoje (
    id_pokoju integer NOT NULL,
    id_rodzaju_pokoju integer,
    dostepnosc boolean NOT NULL,
    nazwa character varying(255) NOT NULL,
    id_lokalizacji integer,
    cena_za_dobe numeric(10,2) DEFAULT 0.00 NOT NULL,
    sciezka_zdjecia character varying(255)
);
    DROP TABLE public.pokoje;
       public         heap r       postgres    false            �            1259    16768    pokoje_id_pokoju_seq    SEQUENCE     �   CREATE SEQUENCE public.pokoje_id_pokoju_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE public.pokoje_id_pokoju_seq;
       public               postgres    false    226            c           0    0    pokoje_id_pokoju_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE public.pokoje_id_pokoju_seq OWNED BY public.pokoje.id_pokoju;
          public               postgres    false    225            �            1259    16731    rodzajpokoju    TABLE     �   CREATE TABLE public.rodzajpokoju (
    id_rodzaj integer NOT NULL,
    nazwa character varying(255) NOT NULL,
    wyposazenie character varying(255),
    ilosc_metrow integer
);
     DROP TABLE public.rodzajpokoju;
       public         heap r       postgres    false            �            1259    16730    rodzajpokoju_id_rodzaj_seq    SEQUENCE     �   CREATE SEQUENCE public.rodzajpokoju_id_rodzaj_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 1   DROP SEQUENCE public.rodzajpokoju_id_rodzaj_seq;
       public               postgres    false    218            d           0    0    rodzajpokoju_id_rodzaj_seq    SEQUENCE OWNED BY     Y   ALTER SEQUENCE public.rodzajpokoju_id_rodzaj_seq OWNED BY public.rodzajpokoju.id_rodzaj;
          public               postgres    false    217            �            1259    16786    wynajem    TABLE     �   CREATE TABLE public.wynajem (
    id_wynajmu integer NOT NULL,
    id_pokoju integer,
    id_klienta integer,
    data_rozpoczecia date NOT NULL,
    data_zakoczenia date,
    id_platnosci integer
);
    DROP TABLE public.wynajem;
       public         heap r       postgres    false            �            1259    16785    wynajem_id_wynajmu_seq    SEQUENCE     �   CREATE SEQUENCE public.wynajem_id_wynajmu_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 -   DROP SEQUENCE public.wynajem_id_wynajmu_seq;
       public               postgres    false    228            e           0    0    wynajem_id_wynajmu_seq    SEQUENCE OWNED BY     Q   ALTER SEQUENCE public.wynajem_id_wynajmu_seq OWNED BY public.wynajem.id_wynajmu;
          public               postgres    false    227            �           2604    16752    klienci id_klienta    DEFAULT     x   ALTER TABLE ONLY public.klienci ALTER COLUMN id_klienta SET DEFAULT nextval('public.klienci_id_klienta_seq'::regclass);
 A   ALTER TABLE public.klienci ALTER COLUMN id_klienta DROP DEFAULT;
       public               postgres    false    221    222    222            �           2604    16743    lokalizacja id_lokalizacji    DEFAULT     �   ALTER TABLE ONLY public.lokalizacja ALTER COLUMN id_lokalizacji SET DEFAULT nextval('public.lokalizacja_id_lokalizacji_seq'::regclass);
 I   ALTER TABLE public.lokalizacja ALTER COLUMN id_lokalizacji DROP DEFAULT;
       public               postgres    false    220    219    220            �           2604    16764    platnosci id_platnosci    DEFAULT     �   ALTER TABLE ONLY public.platnosci ALTER COLUMN id_platnosci SET DEFAULT nextval('public.platnosci_id_platnosci_seq'::regclass);
 E   ALTER TABLE public.platnosci ALTER COLUMN id_platnosci DROP DEFAULT;
       public               postgres    false    224    223    224            �           2604    16772    pokoje id_pokoju    DEFAULT     t   ALTER TABLE ONLY public.pokoje ALTER COLUMN id_pokoju SET DEFAULT nextval('public.pokoje_id_pokoju_seq'::regclass);
 ?   ALTER TABLE public.pokoje ALTER COLUMN id_pokoju DROP DEFAULT;
       public               postgres    false    225    226    226            �           2604    16734    rodzajpokoju id_rodzaj    DEFAULT     �   ALTER TABLE ONLY public.rodzajpokoju ALTER COLUMN id_rodzaj SET DEFAULT nextval('public.rodzajpokoju_id_rodzaj_seq'::regclass);
 E   ALTER TABLE public.rodzajpokoju ALTER COLUMN id_rodzaj DROP DEFAULT;
       public               postgres    false    217    218    218            �           2604    16789    wynajem id_wynajmu    DEFAULT     x   ALTER TABLE ONLY public.wynajem ALTER COLUMN id_wynajmu SET DEFAULT nextval('public.wynajem_id_wynajmu_seq'::regclass);
 A   ALTER TABLE public.wynajem ALTER COLUMN id_wynajmu DROP DEFAULT;
       public               postgres    false    227    228    228            S          0    16749    klienci 
   TABLE DATA           ~   COPY public.klienci (id_klienta, imie, nazwisko, ulica, numer_budynku, pesel, czy_aktywny, email, telefon, haslo) FROM stdin;
    public               postgres    false    222   B       Q          0    16740    lokalizacja 
   TABLE DATA           V   COPY public.lokalizacja (id_lokalizacji, nazwa_lokalizacji, kraj, miasto) FROM stdin;
    public               postgres    false    220   .C       U          0    16761 	   platnosci 
   TABLE DATA           @   COPY public.platnosci (id_platnosci, status, kwota) FROM stdin;
    public               postgres    false    224   �C       W          0    16769    pokoje 
   TABLE DATA           �   COPY public.pokoje (id_pokoju, id_rodzaju_pokoju, dostepnosc, nazwa, id_lokalizacji, cena_za_dobe, sciezka_zdjecia) FROM stdin;
    public               postgres    false    226   D       O          0    16731    rodzajpokoju 
   TABLE DATA           S   COPY public.rodzajpokoju (id_rodzaj, nazwa, wyposazenie, ilosc_metrow) FROM stdin;
    public               postgres    false    218   E       Y          0    16786    wynajem 
   TABLE DATA           u   COPY public.wynajem (id_wynajmu, id_pokoju, id_klienta, data_rozpoczecia, data_zakoczenia, id_platnosci) FROM stdin;
    public               postgres    false    228   F       f           0    0    klienci_id_klienta_seq    SEQUENCE SET     D   SELECT pg_catalog.setval('public.klienci_id_klienta_seq', 4, true);
          public               postgres    false    221            g           0    0    lokalizacja_id_lokalizacji_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('public.lokalizacja_id_lokalizacji_seq', 3, true);
          public               postgres    false    219            h           0    0    platnosci_id_platnosci_seq    SEQUENCE SET     H   SELECT pg_catalog.setval('public.platnosci_id_platnosci_seq', 3, true);
          public               postgres    false    223            i           0    0    pokoje_id_pokoju_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.pokoje_id_pokoju_seq', 8, true);
          public               postgres    false    225            j           0    0    rodzajpokoju_id_rodzaj_seq    SEQUENCE SET     H   SELECT pg_catalog.setval('public.rodzajpokoju_id_rodzaj_seq', 5, true);
          public               postgres    false    217            k           0    0    wynajem_id_wynajmu_seq    SEQUENCE SET     D   SELECT pg_catalog.setval('public.wynajem_id_wynajmu_seq', 3, true);
          public               postgres    false    227            �           2606    16904    klienci klienci_email_unique 
   CONSTRAINT     X   ALTER TABLE ONLY public.klienci
    ADD CONSTRAINT klienci_email_unique UNIQUE (email);
 F   ALTER TABLE ONLY public.klienci DROP CONSTRAINT klienci_email_unique;
       public                 postgres    false    222            �           2606    16759    klienci klienci_pesel_key 
   CONSTRAINT     U   ALTER TABLE ONLY public.klienci
    ADD CONSTRAINT klienci_pesel_key UNIQUE (pesel);
 C   ALTER TABLE ONLY public.klienci DROP CONSTRAINT klienci_pesel_key;
       public                 postgres    false    222            �           2606    16757    klienci klienci_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.klienci
    ADD CONSTRAINT klienci_pkey PRIMARY KEY (id_klienta);
 >   ALTER TABLE ONLY public.klienci DROP CONSTRAINT klienci_pkey;
       public                 postgres    false    222            �           2606    16747    lokalizacja lokalizacja_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY public.lokalizacja
    ADD CONSTRAINT lokalizacja_pkey PRIMARY KEY (id_lokalizacji);
 F   ALTER TABLE ONLY public.lokalizacja DROP CONSTRAINT lokalizacja_pkey;
       public                 postgres    false    220            �           2606    16767    platnosci platnosci_pkey 
   CONSTRAINT     `   ALTER TABLE ONLY public.platnosci
    ADD CONSTRAINT platnosci_pkey PRIMARY KEY (id_platnosci);
 B   ALTER TABLE ONLY public.platnosci DROP CONSTRAINT platnosci_pkey;
       public                 postgres    false    224            �           2606    16774    pokoje pokoje_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY public.pokoje
    ADD CONSTRAINT pokoje_pkey PRIMARY KEY (id_pokoju);
 <   ALTER TABLE ONLY public.pokoje DROP CONSTRAINT pokoje_pkey;
       public                 postgres    false    226            �           2606    16738    rodzajpokoju rodzajpokoju_pkey 
   CONSTRAINT     c   ALTER TABLE ONLY public.rodzajpokoju
    ADD CONSTRAINT rodzajpokoju_pkey PRIMARY KEY (id_rodzaj);
 H   ALTER TABLE ONLY public.rodzajpokoju DROP CONSTRAINT rodzajpokoju_pkey;
       public                 postgres    false    218            �           2606    16791    wynajem wynajem_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.wynajem
    ADD CONSTRAINT wynajem_pkey PRIMARY KEY (id_wynajmu);
 >   ALTER TABLE ONLY public.wynajem DROP CONSTRAINT wynajem_pkey;
       public                 postgres    false    228            �           2606    16780 !   pokoje pokoje_id_lokalizacji_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.pokoje
    ADD CONSTRAINT pokoje_id_lokalizacji_fkey FOREIGN KEY (id_lokalizacji) REFERENCES public.lokalizacja(id_lokalizacji) ON DELETE CASCADE;
 K   ALTER TABLE ONLY public.pokoje DROP CONSTRAINT pokoje_id_lokalizacji_fkey;
       public               postgres    false    220    4779    226            �           2606    16775 $   pokoje pokoje_id_rodzaju_pokoju_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.pokoje
    ADD CONSTRAINT pokoje_id_rodzaju_pokoju_fkey FOREIGN KEY (id_rodzaju_pokoju) REFERENCES public.rodzajpokoju(id_rodzaj) ON DELETE CASCADE;
 N   ALTER TABLE ONLY public.pokoje DROP CONSTRAINT pokoje_id_rodzaju_pokoju_fkey;
       public               postgres    false    218    226    4777            �           2606    16797    wynajem wynajem_id_klienta_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.wynajem
    ADD CONSTRAINT wynajem_id_klienta_fkey FOREIGN KEY (id_klienta) REFERENCES public.klienci(id_klienta) ON DELETE CASCADE;
 I   ALTER TABLE ONLY public.wynajem DROP CONSTRAINT wynajem_id_klienta_fkey;
       public               postgres    false    228    222    4785            �           2606    16802 !   wynajem wynajem_id_platnosci_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.wynajem
    ADD CONSTRAINT wynajem_id_platnosci_fkey FOREIGN KEY (id_platnosci) REFERENCES public.platnosci(id_platnosci) ON DELETE SET NULL;
 K   ALTER TABLE ONLY public.wynajem DROP CONSTRAINT wynajem_id_platnosci_fkey;
       public               postgres    false    228    224    4787            �           2606    16792    wynajem wynajem_id_pokoju_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.wynajem
    ADD CONSTRAINT wynajem_id_pokoju_fkey FOREIGN KEY (id_pokoju) REFERENCES public.pokoje(id_pokoju) ON DELETE CASCADE;
 H   ALTER TABLE ONLY public.wynajem DROP CONSTRAINT wynajem_id_pokoju_fkey;
       public               postgres    false    226    4789    228            S     x�=��N�0E���<�d�Pv��BB�L]C����B��������c͙3W���V��a��:G;3�R�m#��T^�FPb3��.�'��d:���P(F.E�[:�!θ�z>9<�ގF�ճ'��m+k������f����y��ز�8�E���-{^,��ȡ����1�x�*H��<ִ��:��|U��6k�)�!�N���<����[rx�&Ύ��d�s��[/N�W��M�8~��=jY���_[�M&��1�q�      Q   �   x�=�;
1 �:s�!XPO�`+l��͐�$��)��{i��{u�읠���>8���n���R�f���D�s����z�PR�{\(�F+�IM��5���+l��	i�p�{��o��} �.�.�      U   9   x�3�r8�������ill�g`�e����������in
3F35���qqq 	Fs      W   �   x�}�Mn� �5>��c{%�t)YvCc��r���Z=B.�)�{Q��>f��yv����rȁo��C���<�V*�s��A��=���d��5k m�["�X���׮�SCy��@�`�P����7C�!����vԓ׃q�˰���,NL2QG�r�,6�'��H��R����~�Ӎ�0UpjznQGE���:��ና�-EE>��$��B�	d��I�� L�      O     x����J1���)Rz���݁�p6W	V6�I�lvg��!$���p�����{�E9PO�j`f���19�j�D-䃸��T1�[*�
(���a��,e�0}3�6�#dBQۍ{���;�2mC4�a���*�� 4�O��}&\<tG*�87ۈF��+�d����7�H#�>]��
��X~�W���J/R�C���1����A�A���5'�2���u|>8�1�%���ڶ���I#�Gv:��K�\�s��;�9��      Y   >   x�M��	�@�s���$a����a���G0��zJ�1������lz��^�?�}�| ��/     