package com.scorchedcode.ArkDinoBot;

public enum CreatureTypes {
    /*Abberant_Coelacanth,
    Aberrant_Achatina,
    Aberrant_Anglerfish,
    Aberrant_Ankylosaurus,
    Aberrant_Araneo,
    Aberrant_Arthropluera,
    Aberrant_Baryonyx,
    Aberrant_Beelzebufo,
    Aberrant_Carbonemys,
    Aberrant_Carnotaurus,
    Aberrant_Cnidaria,
    Aberrant_Dimetrodon,
    Aberrant_Dimorphodon,
    Aberrant_Diplocaulus,
    Aberrant_Diplodocus,
    Aberrant_Dire_Bear,
    Aberrant_Dodo,
    Aberrant_Doedicurus,
    Aberrant_Dung_Beetle,
    Aberrant_Electrophorus,
    Aberrant_Equus,
    Aberrant_Gigantopithecus,
    Aberrant_Iguanodon,
    Aberrant_Lystrosaurus,
    Aberrant_Manta,
    Aberrant_Megalania,
    Aberrant_Megalosaurus,
    Aberrant_Meganeura,
    Aberrant_Moschops,
    Aberrant_Otter,
    Aberrant_Ovis,
    Aberrant_Paraceratherium,
    Aberrant_Parasaur,
    Aberrant_Piranha,
    Aberrant_Pulmonoscorpius,
    Aberrant_Purlovia,
    Aberrant_Raptor,
    Aberrant_Salmon,
    Aberrant_Sarco,
    Aberrant_Spino,
    Aberrant_Stegosaurus,
    Aberrant_Titanoboa,
    Aberrant_Triceratops,
    Aberrant_Trilobite,
    Achatina,
    Allosaurus,
    Alpha_Basilisk,
    Alpha_Carnotaurus,
    Alpha_Deathworm,
    Alpha_Fire_Wyvern,
    Alpha_Karkinos,
    Alpha_King_Titan,
    Alpha_Leedsichthys,
    Alpha_Megalodon,
    Alpha_Mosasaur,
    Alpha_Raptor,
    Alpha_Surface_Reaper_King,
    Alpha_TRex,
    Alpha_Tusoteuthis,
    Ammonite,
    Anglerfish,
    Ankylosaurus,
    Araneo,
    Archaeopteryx,
    Argentavis,
    Arthropluera,
    Attack_Drone,
    Baryonyx,
    Basilisk,
    Basilosaurus,
    Beelzebufo,
    Beelzemorbus,
    Beta_King_Titan,
    Blade_Wasp,
    Bone_Fire_Wyvern,
    Brontosaurus,
    Broodgenetrix,
    Broodmother_Lysrix,
    Broodmother_Lysrix_Alpha,
    Broodmother_Lysrix_Beta,
    Broodmother_Lysrix_Gamma,
    Bulbdog,
    Bunny_Dodo,
    Bunny_Oviraptor,
    Carbonemys,
    Carnotaurus,
    Castoroides,
    Chalicotherium,
    Chalk_Golem,
    Cnidaria,
    Coelacanth,
    Compy,
    Corrupt_Tumor,
    Corrupted_Arthropluera,
    Corrupted_Carnotaurus,
    Corrupted_Chalicotherium,
    Corrupted_Dilophosaur,
    Corrupted_Dimorphodon,
    Corrupted_Giganotosaurus,
    Corrupted_Paraceratherium,
    Corrupted_Pteranodon,
    Corrupted_Raptor,
    Corrupted_Reaper_King,
    Corrupted_Rex,
    Corrupted_Rock_Drake,
    Corrupted_Spino,
    Corrupted_Stegosaurus,
    Corrupted_Triceratops,
    Corrupted_Wyvern,
    Cubozoa_Multis,
    Daeodon,
    Deathworm,
    Defender,
    Defense_Unit,
    Deinonychus,
    Desert_Titan,
    Desert_Titan_Flock,
    Dilophosaur,
    Dimetrodon,
    Dimorphodon,
    Diplocaulus,
    Diplodocus,
    Dire_Polar_Bear,
    Direbear,
    Direwolf,
    Diseased_Leech,
    Dodo,
    Dodo_Wyvern,
    DodoRex,
    Doedicurus,
    Dragon,
    Dragon_Alpha,
    Dragon_Beta,
    Dragon_Gamma,
    Dung_Beetle,
    Dunkleosteus,
    Eerie_Achatina,
    Eerie_Allosaurus,
    Eerie_Ankylo,
    Eerie_Araneo,
    Eerie_Archaeopteryx,
    Eerie_Argentavis,
    Eerie_Arthropluera,
    Eerie_Baryonyx,
    Eerie_Beelzebufo,
    Eerie_Bronto,
    Eerie_Carbonemys,
    Eerie_Carno,
    Eerie_Castoroides,
    Eerie_Chalicotherium,
    Eerie_Compy,
    Eerie_Daeodon,
    Eerie_Dilophosaurus,
    Eerie_Dimetrodon,
    Eerie_Dimorphodon,
    Eerie_Diplocaulus,
    Eerie_Diplodocus,
    Eerie_Dire_Bear,
    Eerie_Dire_Wolf,
    Eerie_Dodo,
    Eerie_Dung_Beetle,
    Eerie_Equus,
    Eerie_Gallimimus,
    Eerie_Griffin,
    Eerie_Iguanodon,
    Eerie_Jerboa,
    Eerie_Kairuku,
    Eerie_Kaprosuchus,
    Eerie_Lystrosaurus,
    Eerie_Mammoth,
    Eerie_Megaloceros,
    Eerie_Megalosaurus,
    Eerie_Meganeura,
    Eerie_Megatherium,
    Eerie_Mesopithecus,
    Eerie_Moschops,
    Eerie_Onyc,
    Eerie_Otter,
    Eerie_Oviraptor,
    Eerie_Pachy,
    Eerie_Pachyrhinosaurus,
    Eerie_Parasaur,
    Eerie_Pegomastax,
    Eerie_Pelagornis,
    Eerie_Phiomia,
    Eerie_Procoptodon,
    Eerie_Pteranodon,
    Eerie_Pulmonoscorpius,
    Eerie_Purlovia,
    Eerie_Quetzal,
    Eerie_Raptor,
    Eerie_Rex,
    Eerie_Sabertooth,
    Eerie_Sarco,
    Eerie_Spino,
    Eerie_Stego,
    Eerie_Tapejara,
    Eerie_Terror_Bird,
    Eerie_Therizinosaur,
    Eerie_Titanoboa,
    Eerie_Titanomyrma_Drone,
    Eerie_Titanomyrma_Soldier,
    Eerie_Triceratops,
    Eerie_Troodon,
    Eerie_Woolly_Rhinoceros,
    Electrophorus,
    Elemental_Reaper_King,
    Enforcer,
    Enraged_Corrupted_Rex,
    Enraged_Triceratops,
    Equus,
    Eurypterid,
    Featherlight,
    Fire_Wyvern,
    Forest_Titan,
    Forest_Wyvern,
    Gacha,
    GachaClaus,
    Gallimimus,
    Gamma_King_Titan,
    Gasbags,
    Giant_Bee,
    Giant_Queen_Bee,
    Giant_Tortoise,
    Giganotosaurus,
    Gigantopithecus,
    Glowbug,
    Glowtail,
    Griffin,
    Hesperornis,
    Human,
    Hyaenodon,
    Ice_Golem,
    Ice_Titan,
    Ice_Wyvern,
    Iceworm_Male,
    Iceworm_Queen,
    Ichthyornis,
    Ichthyosaurus,
    Iguanodon,
    Jerboa,
    Jug_Bug,
    Kairuku,
    Kaprosuchus,
    Karkinos,
    Kentrosaurus,
    King_Titan,
    Lamprey,
    Lava_Elemental,
    Leech,
    Leedsichthys,
    Lightning_Wyvern,
    Liopleurodon,
    Lymantria,
    Lystrosaurus,
    Magma_Drake,
    Mammoth,
    Managarmr,
    Manta,
    Manticore,
    Manticore_Alpha,
    Manticore_Beta,
    Manticore_Gamma,
    Mantis,
    Mega_Mek,
    Megalania,
    Megaloceros,
    Megalodon,
    Megalosaurus,
    Meganeura,
    Megapithecus,
    Megapithecus_Alpha,
    Megapithecus_Beta,
    Megapithecus_Gamma,
    Megapithecus_Pestis,
    Megatherium,
    Mek,
    Mesopithecus,
    Microraptor,
    Morellatops,
    Mosasaurus,
    Moschops,
    Nameless,
    Noctis,
    Oil_Jug_Bug,
    Onyc,
    Otter,
    Overseer,
    Overseer_Alpha,
    Overseer_Beta,
    Overseer_Gamma,
    Oviraptor,
    Ovis,
    Pachy,
    Pachyrhinosaurus,
    Paraceratherium,
    Parasaur,
    Pegomastax,
    Pelagornis,
    Phiomia,
    Phoenix,
    Piranha,
    Plesiosaur,
    Poison_Wyvern,
    Polar_Bear,
    Polar_Purlovia,
    Procoptodon,
    Pteranodon,
    Pulmonoscorpius,
    Purlovia,
    Quetzal,
    Raptor,
    Ravager,
    Reaper,
    Reaper_King,
    Reaper_Queen,
    Rex,
    Rock_Drake,
    Rock_Elemental,
    Rockwell,
    Rockwell_Alpha,
    Rockwell_Beta,
    Rockwell_Gamma,
    Roll_Rat,
    Royal_Griffin,
    Rubble_Golem,
    Sabertooth,
    Sabertooth_Salmon,
    Sarco,
    Scout,
    Seeker,
    Shapeshifter,
    Shinehorn,
    Skeletal_Bronto,
    Skeletal_Carnotaurus,
    Skeletal_Giganotosaurus,
    Skeletal_Jerboa,
    Skeletal_Quetzal,
    Skeletal_Raptor,
    Skeletal_Rex,
    Skeletal_Stego,
    Skeletal_Trike,
    Snow_Owl,
    Spinosaur,
    Spirit_Dire_Bear,
    Spirit_Direwolf,
    Stegosaurus,
    Subterranean_Reaper_King,
    Super_Turkey,
    Surface_Reaper_King,
    Tapejara,
    Tek_Parasaur,
    Tek_Quetzal,
    Tek_Raptor,
    Tek_Rex,
    Tek_Stegosaurus,
    Terror_Bird,
    Therizinosaur,
    Thorny_Dragon,
    Thylacoleo,
    Titanoboa,
    Titanomyrma,
    Titanomyrma_Drone,
    Titanomyrma_Soldier,
    Titanosaur,
    Triceratops,
    Trilobite,
    Troodon,
    Tusoteuthis,
    Unicorn,
    Velonasaur,
    Vulture,
    Water_Jug_Bug,
    Woolly_Rhino,
    Wyvern,
    Yeti,
    Yutyrannus,
    /*Zombie_Fire_Wyvern,
    Zombie_Lightning_Wyvern,
    Zombie_Poison_Wyvern,
    Zombie_Wyvern,
    Zomdodo;*/
    Achatina,
    Allosaurus,
    Ammonite,
    Angler,
    Ankylosaurus,
    Araneo,
    Archaeopteryx,
    Argentavis,
    Arthropluera,
    Attack_Drone,
    Baryonyx,
    Basilisk,
    Basilosaurus,
    Beelzebufo,
    Brontosaurus,
    Broodmother_Lysrix,
    Bulbdog,
    Carbonemys,
    Carnotaurus,
    Castoroides,
    Chalicotherium,
    Cnidaria,
    Coelacanth,
    Compy,
    Daeodon,
    Deathworm,
    Defense_Unit,
    Deinonychus,
    Desert_Titan,
    Dilophosaur,
    Dimetrodon,
    Dimorphodon,
    Diplocaulus,
    Diplodocus,
    Direbear,
    Direwolf,
    Dodo,
    Doedicurus,
    Dragon,
    Dung_Beetle,
    Dunkleosteus,
    Electrophorus,
    Enforcer,
    Equus,
    Eurypterid,
    Featherlight,
    Forest_Titan,
    Gacha,
    Gallimimus,
    Gasbags,
    Giant_Bee,
    Giganotosaurus,
    Gigantopithecus,
    Glowbug,
    Glowtail,
    Griffin,
    Hesperornis,
    Hyaenodon,
    Ice_Titan,
    Ichthyornis,
    Ichthyosaurus,
    Iguanodon,
    Jerboa,
    Jug_Bug,
    Kairuku,
    Kaprosuchus,
    Karkinos,
    Kentrosaurus,
    Lamprey,
    Leech,
    Leedsichthys,
    Liopleurodon,
    Lymantria,
    Lystrosaurus,
    Mammoth,
    Managarmr,
    Manta,
    Manticore,
    Mantis,
    Mega_Mek,
    Megalania,
    Megaloceros,
    Megalodon,
    Megalosaurus,
    Meganeura,
    Megapithecus,
    Megatherium,
    Mek,
    Mesopithecus,
    Microraptor,
    Morellatops,
    Mosasaurus,
    Moschops,
    Nameless,
    Onyc,
    Otter,
    Overseer,
    Oviraptor,
    Ovis,
    Pachy,
    Pachyrhinosaurus,
    Paraceratherium,
    Parasaur,
    Pegomastax,
    Pelagornis,
    Phiomia,
    Phoenix,
    Piranha,
    Plesiosaur,
    Procoptodon,
    Pteranodon,
    Pulmonoscorpius,
    Purlovia,
    Quetzal,
    Raptor,
    Ravager,
    Reaper,
    Rex,
    Rock_Drake,
    Rock_Elemental,
    Roll_Rat,
    Royal_Griffin,
    Sabertooth,
    Sabertooth_Salmon,
    Sarco,
    Scout,
    Seeker,
    Shinehorn,
    Snow_Owl,
    Spinosaur,
    Stegosaurus,
    Tapejara,
    Terror_Bird,
    Therizinosaurus,
    Thorny_Dragon,
    Thylacoleo,
    Titanoboa,
    Titanomyrma,
    Titanosaur,
    Triceratops,
    Trilobite,
    Troodon,
    Tusoteuthis,
    Unicorn,
    Velonasaur,
    Vulture,
    Woolly_Rhino,
    Wyvern,
    Yeti,
    Yutyrannus;

    public String getFriendlyName() {
        return this.name().replaceAll("_", " ");
    }

    public String getFormattedHandle() {
        return this.name().replaceAll("_", "").toLowerCase();
    }

}
