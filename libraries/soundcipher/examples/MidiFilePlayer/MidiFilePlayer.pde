import arb.soundcipher.*;

SoundCipher sc = new SoundCipher(this);

//sc.playMidiFile("minimalism.mid", 220);
sc.score.addMidiStream(sc.app.createInput("minimalism.mid"));
sc.score.play();
