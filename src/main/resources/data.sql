-- =============================================================
-- SCRIPT DE INICIALIZACIÓN — Diagnósticos Médicos CIE-10
-- Se ejecuta automáticamente al arrancar Spring Boot.
-- Usa INSERT IGNORE para ser idempotente en reinicios.
-- Orden: primero los padres (sin parent), luego los hijos.
-- =============================================================

-- =====================================================================
-- BLOQUE 1 — DIAGNÓSTICOS PADRE (parent_diagnostic_id = NULL)
-- =====================================================================

INSERT IGNORE INTO medical_diagnostic (id, diagnostic_code, diagnostic_name, parent_diagnostic_id) VALUES

-- ── Capítulo I: Enfermedades infecciosas y parasitarias (A00–B99) ──
(1,  'A00', 'Cólera', NULL),
(2,  'A01', 'Fiebre tifoidea y paratifoidea', NULL),
(3,  'A02', 'Otras infecciones por Salmonella', NULL),
(4,  'A03', 'Shigelosis', NULL),
(5,  'A04', 'Otras infecciones intestinales bacterianas', NULL),
(6,  'A06', 'Amibiasis', NULL),
(7,  'A09', 'Diarrea y gastroenteritis de presunto origen infeccioso', NULL),
(8,  'A15', 'Tuberculosis pulmonar confirmada bacteriológicamente o histológicamente', NULL),
(9,  'A17', 'Tuberculosis del sistema nervioso', NULL),
(10, 'A18', 'Tuberculosis de otros órganos', NULL),
(11, 'A19', 'Tuberculosis miliar', NULL),
(12, 'A36', 'Difteria', NULL),
(13, 'A37', 'Tos ferina [pertussis]', NULL),
(14, 'A40', 'Septicemia estreptocócica', NULL),
(15, 'A41', 'Otra septicemia', NULL),
(16, 'A46', 'Erisipela', NULL),
(17, 'B00', 'Infecciones herpéticas [herpes simple]', NULL),
(18, 'B01', 'Varicela', NULL),
(19, 'B02', 'Herpes zóster', NULL),
(20, 'B05', 'Sarampión', NULL),
(21, 'B06', 'Rubéola [sarampión alemán]', NULL),
(22, 'B15', 'Hepatitis A aguda', NULL),
(23, 'B16', 'Hepatitis B aguda', NULL),
(24, 'B18', 'Hepatitis viral crónica', NULL),
(25, 'B20', 'Enfermedad por VIH resultante en enfermedades infecciosas y parasitarias', NULL),

-- ── Capítulo II: Neoplasias (C00–D49) ──
(26, 'C00', 'Neoplasia maligna del labio', NULL),
(27, 'C18', 'Neoplasia maligna del colon', NULL),
(28, 'C20', 'Neoplasia maligna del recto', NULL),
(29, 'C22', 'Neoplasia maligna del hígado y vías biliares intrahepáticas', NULL),
(30, 'C25', 'Neoplasia maligna del páncreas', NULL),
(31, 'C34', 'Neoplasia maligna de los bronquios y del pulmón', NULL),
(32, 'C50', 'Neoplasia maligna de la mama', NULL),
(33, 'C53', 'Neoplasia maligna del cuello del útero', NULL),
(34, 'C61', 'Neoplasia maligna de la próstata', NULL),
(35, 'C67', 'Neoplasia maligna de la vejiga urinaria', NULL),
(36, 'C71', 'Neoplasia maligna del encéfalo', NULL),

-- ── Capítulo IX: Enfermedades del sistema circulatorio (I00–I99) ──
(37, 'I10', 'Hipertensión esencial (primaria)', NULL),
(38, 'I20', 'Angina de pecho', NULL),
(39, 'I21', 'Infarto agudo del miocardio', NULL),
(40, 'I25', 'Enfermedad isquémica crónica del corazón', NULL),
(41, 'I48', 'Fibrilación y aleteo auricular', NULL),
(42, 'I50', 'Insuficiencia cardíaca', NULL),
(43, 'I63', 'Infarto cerebral', NULL),
(44, 'I64', 'Accidente vascular encefálico no especificado como hemorrágico o isquémico', NULL),
(45, 'I65', 'Oclusión y estenosis de las arterias precerebrales sin infarto cerebral', NULL),

-- ── Capítulo X: Enfermedades del sistema respiratorio (J00–J99) ──
(46, 'J00', 'Rinofaringitis aguda [resfriado común]', NULL),
(47, 'J01', 'Sinusitis aguda', NULL),
(48, 'J02', 'Faringitis aguda', NULL),
(49, 'J03', 'Amigdalitis aguda', NULL),
(50, 'J04', 'Laringitis y traqueítis agudas', NULL),
(51, 'J06', 'Infección aguda de las vías respiratorias superiores, sitios múltiples o no especificados', NULL),
(52, 'J10', 'Influenza debida a virus de la influenza identificado', NULL),
(53, 'J11', 'Influenza debida a virus no identificado', NULL),
(54, 'J18', 'Neumonía, no especificada', NULL),
(55, 'J20', 'Bronquitis aguda', NULL),
(56, 'J44', 'Enfermedad pulmonar obstructiva crónica', NULL),
(57, 'J45', 'Asma', NULL),
(58, 'J47', 'Bronquiectasia', NULL),

-- ── Capítulo XI: Enfermedades del sistema digestivo (K00–K93) ──
(59, 'K25', 'Úlcera gástrica', NULL),
(60, 'K26', 'Úlcera duodenal', NULL),
(61, 'K29', 'Gastritis y duodenitis', NULL),
(62, 'K35', 'Apendicitis aguda', NULL),
(63, 'K40', 'Hernia inguinal', NULL),
(64, 'K70', 'Enfermedad hepática alcohólica', NULL),
(65, 'K72', 'Insuficiencia hepática, no clasificada en otra parte', NULL),
(66, 'K74', 'Fibrosis y cirrosis del hígado', NULL),
(67, 'K80', 'Colelitiasis', NULL),
(68, 'K81', 'Colecistitis', NULL),

-- ── Capítulo XIII: Enfermedades del sistema musculoesquelético (M00–M99) ──
(69, 'M06', 'Otras artritis reumatoides', NULL),
(70, 'M10', 'Gota', NULL),
(71, 'M15', 'Poliartrosis', NULL),
(72, 'M17', 'Gonartrosis [artrosis de la rodilla]', NULL),
(73, 'M41', 'Escoliosis', NULL),
(74, 'M47', 'Espondiloartrosis', NULL),
(75, 'M54', 'Dorsalgia', NULL),

-- ── Capítulo XIV: Enfermedades del sistema genitourinario (N00–N99) ──
(76, 'N18', 'Enfermedad renal crónica', NULL),
(77, 'N20', 'Cálculo del riñón y del uréter', NULL),
(78, 'N39', 'Otras afecciones del sistema urinario', NULL),
(79, 'N40', 'Hiperplasia de la próstata', NULL),
(80, 'N80', 'Endometriosis', NULL),
(81, 'N92', 'Menstruación excesiva, frecuente e irregular', NULL),

-- ── Capítulo XIX: Traumatismos, envenenamientos (S00–T88) ──
(82, 'S06', 'Traumatismo intracraneal', NULL),
(83, 'S52', 'Fractura del hueso del antebrazo', NULL),
(84, 'S72', 'Fractura del fémur', NULL),
(85, 'S82', 'Fractura de la pierna, incluido el tobillo', NULL),
(86, 'T14', 'Traumatismo de región no especificada del cuerpo', NULL);


-- =====================================================================
-- BLOQUE 2 — DIAGNÓSTICOS HIJO (con parent_diagnostic_id)
-- =====================================================================

INSERT IGNORE INTO medical_diagnostic (id, diagnostic_code, diagnostic_name, parent_diagnostic_id) VALUES

-- ── A00: Cólera ──
(87,  'A00.0', 'Cólera debida a Vibrio cholerae O1, biotipo cholerae', 1),
(88,  'A00.1', 'Cólera debida a Vibrio cholerae O1, biotipo El Tor', 1),
(89,  'A00.9', 'Cólera, no especificado', 1),

-- ── A01: Fiebre tifoidea y paratifoidea ──
(90,  'A01.0', 'Fiebre tifoidea', 2),
(91,  'A01.1', 'Fiebre paratifoidea A', 2),
(92,  'A01.2', 'Fiebre paratifoidea B', 2),
(93,  'A01.3', 'Fiebre paratifoidea C', 2),
(94,  'A01.4', 'Fiebre paratifoidea, no especificada', 2),

-- ── A02: Otras infecciones por Salmonella ──
(95,  'A02.0', 'Enteritis por Salmonella', 3),
(96,  'A02.1', 'Septicemia debida a Salmonella', 3),
(97,  'A02.2', 'Infecciones localizadas debidas a Salmonella', 3),
(98,  'A02.8', 'Otras infecciones especificadas debidas a Salmonella', 3),
(99,  'A02.9', 'Infección debida a Salmonella, no especificada', 3),

-- ── A06: Amibiasis ──
(100, 'A06.0', 'Disentería amibiana aguda', 6),
(101, 'A06.1', 'Amebiasis crónica intestinal', 6),
(102, 'A06.4', 'Absceso amibiano del hígado', 6),
(103, 'A06.6', 'Amibiasis cutánea', 6),

-- ── A15: Tuberculosis pulmonar confirmada ──
(104, 'A15.0', 'Tuberculosis del pulmón, confirmada por microscopía de esputo con o sin cultivo', 8),
(105, 'A15.1', 'Tuberculosis del pulmón, confirmada únicamente por cultivo', 8),
(106, 'A15.2', 'Tuberculosis del pulmón, confirmada histológicamente', 8),
(107, 'A15.3', 'Tuberculosis del pulmón, confirmada por medios no especificados', 8),

-- ── A17: Tuberculosis del sistema nervioso ──
(108, 'A17.0', 'Meningitis tuberculosa', 9),
(109, 'A17.1', 'Tuberculoma meníngeo', 9),

-- ── A37: Tos ferina ──
(110, 'A37.0', 'Tos ferina debida a Bordetella pertussis', 13),
(111, 'A37.1', 'Tos ferina debida a Bordetella parapertussis', 13),
(112, 'A37.9', 'Tos ferina, no especificada', 13),

-- ── A41: Otra septicemia ──
(113, 'A41.0', 'Septicemia debida a Staphylococcus aureus', 15),
(114, 'A41.1', 'Septicemia debida a Staphylococcus coagulasa-negativo', 15),
(115, 'A41.2', 'Septicemia debida a Staphylococcus no especificado', 15),
(116, 'A41.3', 'Septicemia debida a Haemophilus influenzae', 15),
(117, 'A41.4', 'Septicemia debida a anaerobios', 15),
(118, 'A41.5', 'Septicemia debida a otros organismos gramnegativos', 15),
(119, 'A41.8', 'Otras septicemias especificadas', 15),
(120, 'A41.9', 'Septicemia, no especificada', 15),

-- ── B00: Infecciones herpéticas ──
(121, 'B00.0', 'Eccema herpético', 17),
(122, 'B00.1', 'Dermatitis vesicular herpética', 17),
(123, 'B00.2', 'Gingivoestomatitis y faringoamigdalitis herpética', 17),
(124, 'B00.3', 'Meningitis herpética', 17),
(125, 'B00.4', 'Encefalitis herpética', 17),
(126, 'B00.5', 'Enfermedad herpética del ojo', 17),
(127, 'B00.7', 'Enfermedad herpética diseminada', 17),
(128, 'B00.9', 'Infección debida a virus del herpes simple, no especificada', 17),

-- ── B01: Varicela ──
(129, 'B01.0', 'Meningitis por varicela', 18),
(130, 'B01.1', 'Encefalitis por varicela', 18),
(131, 'B01.2', 'Neumonía por varicela', 18),
(132, 'B01.9', 'Varicela sin complicaciones', 18),

-- ── B02: Herpes zóster ──
(133, 'B02.0', 'Encefalitis por zóster', 19),
(134, 'B02.1', 'Meningitis por zóster', 19),
(135, 'B02.2', 'Zóster con otras manifestaciones del sistema nervioso', 19),
(136, 'B02.3', 'Zóster ocular', 19),
(137, 'B02.9', 'Zóster sin complicaciones', 19),

-- ── B15: Hepatitis A aguda ──
(138, 'B15.0', 'Hepatitis A con coma hepático', 22),
(139, 'B15.9', 'Hepatitis A sin coma hepático', 22),

-- ── B16: Hepatitis B aguda ──
(140, 'B16.0', 'Hepatitis B aguda con agente delta y con coma hepático', 23),
(141, 'B16.1', 'Hepatitis B aguda con agente delta sin coma hepático', 23),
(142, 'B16.2', 'Hepatitis B aguda sin agente delta, con coma hepático', 23),
(143, 'B16.9', 'Hepatitis B aguda sin agente delta y sin coma hepático', 23),

-- ── B18: Hepatitis viral crónica ──
(144, 'B18.0', 'Hepatitis viral B crónica con agente delta', 24),
(145, 'B18.1', 'Hepatitis viral B crónica sin agente delta', 24),
(146, 'B18.2', 'Hepatitis viral C crónica', 24),
(147, 'B18.8', 'Otras hepatitis virales crónicas', 24),
(148, 'B18.9', 'Hepatitis viral crónica, sin otra especificación', 24),

-- ── C18: Neoplasia maligna del colon ──
(149, 'C18.0', 'Neoplasia maligna del ciego', 27),
(150, 'C18.1', 'Neoplasia maligna del apéndice', 27),
(151, 'C18.2', 'Neoplasia maligna del colon ascendente', 27),
(152, 'C18.3', 'Neoplasia maligna del ángulo hepático del colon', 27),
(153, 'C18.4', 'Neoplasia maligna del colon transverso', 27),
(154, 'C18.5', 'Neoplasia maligna del ángulo esplénico del colon', 27),
(155, 'C18.6', 'Neoplasia maligna del colon descendente', 27),
(156, 'C18.7', 'Neoplasia maligna del colon sigmoide', 27),
(157, 'C18.8', 'Lesión de sitios contiguos del colon', 27),
(158, 'C18.9', 'Neoplasia maligna del colon, parte no especificada', 27),

-- ── C34: Neoplasia maligna de bronquios y pulmón ──
(159, 'C34.0', 'Neoplasia maligna del bronquio principal', 31),
(160, 'C34.1', 'Neoplasia maligna del lóbulo superior, bronquio o pulmón', 31),
(161, 'C34.2', 'Neoplasia maligna del lóbulo medio, bronquio o pulmón', 31),
(162, 'C34.3', 'Neoplasia maligna del lóbulo inferior, bronquio o pulmón', 31),
(163, 'C34.8', 'Lesión de sitios contiguos de los bronquios y del pulmón', 31),
(164, 'C34.9', 'Neoplasia maligna de bronquios y pulmón, parte no especificada', 31),

-- ── C50: Neoplasia maligna de la mama ──
(165, 'C50.0', 'Neoplasia maligna del pezón y aréola', 32),
(166, 'C50.1', 'Neoplasia maligna de la porción central de la mama', 32),
(167, 'C50.2', 'Neoplasia maligna del cuadrante superior interno de la mama', 32),
(168, 'C50.3', 'Neoplasia maligna del cuadrante inferior interno de la mama', 32),
(169, 'C50.4', 'Neoplasia maligna del cuadrante superior externo de la mama', 32),
(170, 'C50.5', 'Neoplasia maligna del cuadrante inferior externo de la mama', 32),
(171, 'C50.6', 'Neoplasia maligna de la prolongación axilar de la mama', 32),
(172, 'C50.8', 'Lesión de sitios contiguos de la mama', 32),
(173, 'C50.9', 'Neoplasia maligna de la mama, parte no especificada', 32),

-- ── C71: Neoplasia maligna del encéfalo ──
(174, 'C71.0', 'Neoplasia maligna del cerebro, excepto lóbulos y ventrículos', 36),
(175, 'C71.1', 'Neoplasia maligna del lóbulo frontal', 36),
(176, 'C71.2', 'Neoplasia maligna del lóbulo temporal', 36),
(177, 'C71.3', 'Neoplasia maligna del lóbulo parietal', 36),
(178, 'C71.4', 'Neoplasia maligna del lóbulo occipital', 36),
(179, 'C71.5', 'Neoplasia maligna del ventrículo cerebral', 36),
(180, 'C71.6', 'Neoplasia maligna del cerebelo', 36),
(181, 'C71.7', 'Neoplasia maligna del tronco encefálico', 36),
(182, 'C71.8', 'Lesión de sitios contiguos del encéfalo', 36),
(183, 'C71.9', 'Neoplasia maligna del encéfalo, parte no especificada', 36),

-- ── I20: Angina de pecho ──
(184, 'I20.0', 'Angina inestable', 38),
(185, 'I20.1', 'Angina de pecho con espasmo documentado', 38),
(186, 'I20.8', 'Otras formas de angina de pecho', 38),
(187, 'I20.9', 'Angina de pecho, no especificada', 38),

-- ── I21: Infarto agudo del miocardio ──
(188, 'I21.0', 'Infarto agudo transmural del miocardio de la pared anterior', 39),
(189, 'I21.1', 'Infarto agudo transmural del miocardio de la pared inferior', 39),
(190, 'I21.2', 'Infarto agudo transmural del miocardio de otros sitios', 39),
(191, 'I21.3', 'Infarto agudo transmural del miocardio de sitio no especificado', 39),
(192, 'I21.4', 'Infarto agudo subendocárdico del miocardio', 39),
(193, 'I21.9', 'Infarto agudo del miocardio, sin otra especificación', 39),

-- ── I25: Enfermedad isquémica crónica del corazón ──
(194, 'I25.0', 'Enfermedad cardiovascular aterosclerótica descrita como tal', 40),
(195, 'I25.1', 'Enfermedad aterosclerótica del corazón', 40),
(196, 'I25.2', 'Infarto antiguo del miocardio', 40),
(197, 'I25.5', 'Miocardiopatía isquémica', 40),
(198, 'I25.6', 'Isquemia silente del miocardio', 40),
(199, 'I25.8', 'Otras formas de enfermedad isquémica crónica del corazón', 40),
(200, 'I25.9', 'Enfermedad isquémica crónica del corazón, no especificada', 40),

-- ── I50: Insuficiencia cardíaca ──
(201, 'I50.0', 'Insuficiencia cardíaca congestiva', 42),
(202, 'I50.1', 'Insuficiencia ventricular izquierda', 42),
(203, 'I50.9', 'Insuficiencia cardíaca, no especificada', 42),

-- ── I63: Infarto cerebral ──
(204, 'I63.0', 'Infarto cerebral debido a trombosis de las arterias precerebrales', 43),
(205, 'I63.1', 'Infarto cerebral debido a embolia de las arterias precerebrales', 43),
(206, 'I63.2', 'Infarto cerebral por oclusión o estenosis no especificada de arterias precerebrales', 43),
(207, 'I63.3', 'Infarto cerebral debido a trombosis de las arterias cerebrales', 43),
(208, 'I63.4', 'Infarto cerebral debido a embolia de las arterias cerebrales', 43),
(209, 'I63.5', 'Infarto cerebral por oclusión o estenosis no especificada de arterias cerebrales', 43),
(210, 'I63.6', 'Infarto cerebral por trombosis de venas cerebrales, no piogénica', 43),
(211, 'I63.8', 'Otro infarto cerebral', 43),
(212, 'I63.9', 'Infarto cerebral, no especificado', 43),

-- ── J01: Sinusitis aguda ──
(213, 'J01.0', 'Sinusitis maxilar aguda', 47),
(214, 'J01.1', 'Sinusitis frontal aguda', 47),
(215, 'J01.2', 'Sinusitis etmoidal aguda', 47),
(216, 'J01.3', 'Sinusitis esfenoidal aguda', 47),
(217, 'J01.4', 'Pansinusitis aguda', 47),
(218, 'J01.8', 'Otras sinusitis agudas', 47),
(219, 'J01.9', 'Sinusitis aguda, no especificada', 47),

-- ── J10: Influenza con virus identificado ──
(220, 'J10.0', 'Influenza con neumonía, virus de la influenza identificado', 52),
(221, 'J10.1', 'Influenza con otras manifestaciones respiratorias, virus identificado', 52),
(222, 'J10.8', 'Influenza con otras manifestaciones, virus de la influenza identificado', 52),

-- ── J11: Influenza con virus no identificado ──
(223, 'J11.0', 'Influenza con neumonía, virus no identificado', 53),
(224, 'J11.1', 'Influenza con otras manifestaciones respiratorias, virus no identificado', 53),
(225, 'J11.8', 'Influenza con otras manifestaciones, virus no identificado', 53),

-- ── J44: Enfermedad pulmonar obstructiva crónica ──
(226, 'J44.0', 'EPOC con infección respiratoria aguda del tracto respiratorio inferior', 56),
(227, 'J44.1', 'EPOC con exacerbación aguda, no especificada', 56),
(228, 'J44.8', 'Otras enfermedades pulmonares obstructivas crónicas especificadas', 56),
(229, 'J44.9', 'Enfermedad pulmonar obstructiva crónica, no especificada', 56),

-- ── J45: Asma ──
(230, 'J45.0', 'Asma predominantemente alérgica', 57),
(231, 'J45.1', 'Asma no alérgica', 57),
(232, 'J45.8', 'Asma mixta', 57),
(233, 'J45.9', 'Asma, no especificada', 57),

-- ── K25: Úlcera gástrica ──
(234, 'K25.0', 'Úlcera gástrica aguda con hemorragia', 59),
(235, 'K25.1', 'Úlcera gástrica aguda con perforación', 59),
(236, 'K25.2', 'Úlcera gástrica aguda con hemorragia y perforación', 59),
(237, 'K25.3', 'Úlcera gástrica aguda sin hemorragia ni perforación', 59),
(238, 'K25.4', 'Úlcera gástrica crónica o no especificada con hemorragia', 59),
(239, 'K25.5', 'Úlcera gástrica crónica o no especificada con perforación', 59),
(240, 'K25.6', 'Úlcera gástrica crónica o no especificada con hemorragia y perforación', 59),
(241, 'K25.7', 'Úlcera gástrica crónica sin hemorragia ni perforación', 59),
(242, 'K25.9', 'Úlcera gástrica no especificada como aguda o crónica, sin hemorragia ni perforación', 59),

-- ── K29: Gastritis y duodenitis ──
(243, 'K29.0', 'Gastritis hemorrágica aguda', 61),
(244, 'K29.1', 'Otras gastritis agudas', 61),
(245, 'K29.2', 'Gastritis alcohólica', 61),
(246, 'K29.3', 'Gastritis crónica superficial', 61),
(247, 'K29.4', 'Gastritis crónica atrófica', 61),
(248, 'K29.5', 'Gastritis crónica, no especificada', 61),
(249, 'K29.6', 'Otras gastritis', 61),
(250, 'K29.7', 'Gastroduodenitis, no especificada', 61),

-- ── K80: Colelitiasis ──
(251, 'K80.0', 'Cálculo de la vesícula biliar con colecistitis aguda', 67),
(252, 'K80.1', 'Cálculo de la vesícula biliar con otras colecistitis', 67),
(253, 'K80.2', 'Cálculo de la vesícula biliar sin colecistitis', 67),
(254, 'K80.3', 'Cálculo del conducto biliar con colangitis', 67),
(255, 'K80.4', 'Cálculo del conducto biliar con colecistitis', 67),
(256, 'K80.5', 'Cálculo del conducto biliar sin colangitis ni colecistitis', 67),

-- ── M06: Otras artritis reumatoides ──
(257, 'M06.0', 'Artritis reumatoide seronegativa', 69),
(258, 'M06.1', 'Enfermedad de Still del adulto', 69),
(259, 'M06.2', 'Bursitis reumatoide', 69),
(260, 'M06.3', 'Nódulo reumatoide', 69),
(261, 'M06.4', 'Poliartropatía inflamatoria', 69),
(262, 'M06.8', 'Otras artritis reumatoides especificadas', 69),
(263, 'M06.9', 'Artritis reumatoide, no especificada', 69),

-- ── M17: Gonartrosis ──
(264, 'M17.0', 'Gonartrosis primaria bilateral', 72),
(265, 'M17.1', 'Otras gonartrosis primarias', 72),
(266, 'M17.2', 'Gonartrosis secundaria postraumática bilateral', 72),
(267, 'M17.3', 'Otras gonartrosis secundarias postraumáticas', 72),
(268, 'M17.4', 'Otras gonartrosis secundarias bilaterales', 72),
(269, 'M17.5', 'Otras gonartrosis secundarias', 72),
(270, 'M17.9', 'Gonartrosis, no especificada', 72),

-- ── M54: Dorsalgia ──
(271, 'M54.1', 'Radiculopatía', 75),
(272, 'M54.2', 'Cervicalgia', 75),
(273, 'M54.3', 'Ciática', 75),
(274, 'M54.4', 'Lumbago con ciática', 75),
(275, 'M54.5', 'Lumbago no especificado', 75),

-- ── N18: Enfermedad renal crónica ──
(276, 'N18.1', 'Enfermedad renal crónica, estadio 1', 76),
(277, 'N18.2', 'Enfermedad renal crónica, estadio 2', 76),
(278, 'N18.3', 'Enfermedad renal crónica, estadio 3', 76),
(279, 'N18.4', 'Enfermedad renal crónica, estadio 4', 76),
(280, 'N18.5', 'Enfermedad renal crónica, estadio 5', 76),
(281, 'N18.9', 'Enfermedad renal crónica, no especificada', 76),

-- ── N39: Otras afecciones del sistema urinario ──
(282, 'N39.0', 'Infección de vías urinarias, sitio no especificado', 78),
(283, 'N39.3', 'Incontinencia urinaria de esfuerzo', 78),
(284, 'N39.4', 'Otras incontinencias urinarias especificadas', 78),

-- ── S06: Traumatismo intracraneal ──
(285, 'S06.0', 'Conmoción cerebral', 82),
(286, 'S06.1', 'Edema cerebral traumático', 82),
(287, 'S06.2', 'Lesión cerebral difusa', 82),
(288, 'S06.3', 'Lesión cerebral focal', 82),

-- ── S72: Fractura del fémur ──
(289, 'S72.0', 'Fractura del cuello del fémur', 84),
(290, 'S72.1', 'Fractura pertrocantérica', 84),
(291, 'S72.2', 'Fractura subtrocantérica', 84),
(292, 'S72.3', 'Fractura de la diáfisis del fémur', 84);


-- =============================================================
-- SCRIPT DE INICIALIZACIÓN — Áreas de Evaluación
-- =============================================================

INSERT IGNORE INTO evaluation_area (id, code_area, name, doctors_count) VALUES
(1, 'CARDIO',   'Cardiología General',                    0),
(2, 'CARDINT',  'Cardiología Intervencionista',           0),
(3, 'NEURO',    'Neuroimagen',                            0),
(4, 'PULMON',   'Neumología y Radiología Torácica',       0),
(5, 'OFTALMO',  'Oftalmología',                           0);


-- =============================================================
-- SCRIPT DE INICIALIZACIÓN — Tipos de Imagen Médica
-- =============================================================

INSERT IGNORE INTO medical_image_type (id, name, evaluation_area_id) VALUES
-- ── CARDIO (id=1) ──
(1,  'Electrocardiograma',                      1),
(2,  'Ecocardiograma',                          1),
-- ── CARDINT (id=2) ──
(3,  'Angiografía Coronaria',                   2),
-- ── NEURO (id=3) ──
(4,  'Resonancia Magnética Cerebral',           3),
(5,  'Tomografía Computarizada Cerebral',       3),
-- ── PULMON (id=4) ──
(6,  'Radiografía de Tórax',                    4),
(7,  'Tomografía Computarizada de Tórax',       4),
-- ── OFTALMO (id=5) ──
(8,  'Retinografía (Fondo de Ojo)',             5),
(9,  'Tomografía de Coherencia Óptica (OCT)',   5),
-- ── Sin área asignada ──
(10, 'Ultrasonido Abdominal',                   NULL),
(11, 'Radiografía de Columna Vertebral',        NULL),
(12, 'Resonancia Magnética de Rodilla',         NULL),
(13, 'Densitometría Ósea',                      NULL);


-- =============================================================
-- SCRIPT DE INICIALIZACIÓN — Datos Primitivos (SystemDatum + PrimitiveDatum)
-- Herencia JOINED: primero se inserta en system_datum (solo id),
-- luego en primitive_datum (id + columnas propias).
-- =============================================================

-- ── Tabla base (herencia JOINED) ──
INSERT IGNORE INTO system_datum (id) VALUES
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10),
(11),(12),(13),(14),(15),(16),(17),(18),(19),(20),
(21),(22),(23),(24),(25),(26),(27),(28),(29),(30),
(31),(32),(33),(34),(35);

-- ── Tabla derivada ──
INSERT IGNORE INTO primitive_datum (id, name, type, unit) VALUES

-- ── Signos vitales ──
(1,  'Frecuencia Cardíaca',                   'DOUBLE',  'BPM'),
(2,  'Presión Arterial Sistólica',            'INTEGER', 'MMHG'),
(3,  'Presión Arterial Diastólica',           'INTEGER', 'MMHG'),
(4,  'Temperatura Corporal',                  'DOUBLE',  'CELSIUS'),
(5,  'Saturación de Oxígeno (SpO2)',          'DOUBLE',  'PERCENT'),
(6,  'Frecuencia Respiratoria',               'INTEGER', 'RESPIRATIONS_PER_MINUTE'),

-- ── Antropometría ──
(7,  'Peso Corporal',                         'DOUBLE',  'KILOGRAM'),
(8,  'Talla',                                 'DOUBLE',  'METER'),
(9,  'Índice de Masa Corporal (IMC)',          'DOUBLE',  'KG_M2'),

-- ── Glucemia ──
(10, 'Glucosa en Ayunas',                     'DOUBLE',  'MG_DL'),
(11, 'Glucosa Postprandial',                  'DOUBLE',  'MG_DL'),
(12, 'Hemoglobina Glicosilada (HbA1c)',       'DOUBLE',  'PERCENT'),

-- ── Perfil lipídico ──
(13, 'Colesterol Total',                      'DOUBLE',  'MG_DL'),
(14, 'Colesterol LDL',                        'DOUBLE',  'MG_DL'),
(15, 'Colesterol HDL',                        'DOUBLE',  'MG_DL'),
(16, 'Triglicéridos',                         'DOUBLE',  'MG_DL'),

-- ── Función renal ──
(17, 'Creatinina Sérica',                     'DOUBLE',  'MG_DL'),
(18, 'Nitrógeno Ureico (BUN)',                'DOUBLE',  'MG_DL'),
(19, 'Tasa de Filtración Glomerular (TFG)',   'DOUBLE',  'NONE'),

-- ── Función hepática ──
(20, 'Alanina Aminotransferasa (ALT)',         'DOUBLE',  'INTERNATIONAL_UNIT'),
(21, 'Aspartato Aminotransferasa (AST)',       'DOUBLE',  'INTERNATIONAL_UNIT'),
(22, 'Bilirrubina Total',                     'DOUBLE',  'MG_DL'),

-- ── Hemograma ──
(23, 'Hemoglobina',                           'DOUBLE',  'GRAM'),
(24, 'Hematocrito',                           'DOUBLE',  'PERCENT'),
(25, 'Leucocitos (Glóbulos Blancos)',          'DOUBLE',  'NONE'),
(26, 'Plaquetas',                             'DOUBLE',  'NONE'),

-- ── Electrolitos ──
(27, 'Sodio Sérico',                          'DOUBLE',  'MMOL_L'),
(28, 'Potasio Sérico',                        'DOUBLE',  'MMOL_L'),
(29, 'Cloro Sérico',                          'DOUBLE',  'MMOL_L'),

-- ── Marcadores cardíacos ──
(30, 'Troponina I',                           'DOUBLE',  'MICROGRAM'),
(31, 'Fracción de Eyección (FE)',             'DOUBLE',  'PERCENT'),

-- ── Tiroides ──
(32, 'TSH (Hormona Estimulante de Tiroides)', 'DOUBLE',  'INTERNATIONAL_UNIT'),
(33, 'T4 Libre',                              'DOUBLE',  'NONE'),

-- ── Orina ──
(34, 'pH Urinario',                           'DOUBLE',  'NONE'),
(35, 'Proteínas en Orina',                    'DOUBLE',  'MG_DL');
