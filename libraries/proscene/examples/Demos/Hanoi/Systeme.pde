class Systeme {
  int[][] etat;
  int situation;
  Disque[] disques;

  Systeme() {
    situation=0;
    etat= new int[4][nbdisques];
    for (int i=0;i<4;i++) {
      for (int j=0;j<nbdisques;j++) {
        etat[i][j]=5;
      }
    }
    for (int j=0;j<nbdisques;j++) {
      etat[0][j]=nbdisques-1-j;
    }
    //construction des disques
    disques = new Disque[nbdisques];
    for (int i=0;i<nbdisques;i++) {
      disques[i]=new Disque(0, i, frames[i]);
    }
  }

  void draw() {
    pilote();
    for (int i=0;i<4;i++) {
      for (int j=0;j<nbdisques;j++) {
        if (getEtat(i, j)!=5)disques[getEtat(i, j)].draw();
      }
    }
    // imprimeEtat();
  }

  int getEtat(int i, int j) {
    return etat[i][j];
  }

  void setEtat(int i, int j, int d) {
    etat[i][j]=d;
  }

  int surLaPile( int numd) {
    int piquet=disques[numd].surLePiquet;
    int reponse=5;
    if ((getEtat(piquet, 0) == numd) &&(getEtat(piquet, 1)) == 5) reponse=0;
    if ((getEtat(piquet, 1) == numd) &&(getEtat(piquet, 2)) == 5) reponse=1;
    if ((getEtat(piquet, 2) == numd) &&(getEtat(piquet, 3)) == 5) reponse=2;
    if ((getEtat(piquet, 3) == numd) &&(getEtat(piquet, 4)) == 5) reponse=3;
    if (getEtat(piquet, 4) == numd) reponse=4;
    return reponse;
  }

  int calculHauteur(int n) {
    int rep=0;
    for (int i=0;i<nbdisques;i++) {
      if (getEtat(n, i)<5) rep+=1;
    }
    return rep;
  }

  void decollage(int disqueEnPrise) {
    int  piquet=disques[disqueEnPrise].surLePiquet;
    int h=surLaPile(disqueEnPrise);
    Vec zplus15= Vec.add(disques[disqueEnPrise].repere.position(), new Vec(0, 0, 15));
    disques[disqueEnPrise].repere.setPosition(zplus15);
    disques[disqueEnPrise].repere.setConstraint(contrainteGuide);
    setEtat(3, 0, disqueEnPrise);
    setEtat(piquet, h, 5);
    situation=1;
  }

  void attenteDeClic() {
    int i=0;
    while ( (i<nbdisques) && (!disques[i].repere.grabsInput(((Scene)scene).motionAgent())))
    {
      i++;
    }

    if (i<nbdisques)
    {//le disque i est cliqué  collerAuPiquet();
      //vérifier que c'est soit un disque de tete soit disque en mouvement

      if ((getEtat(3, 0)==5 )&& (surLaPile(i)<5))
      {
        decollage(i);
        situation =1;
      }//si c'est un disque immobile vérifier qu'il est en tete
    }
  }

  void atterrissage() {
    int  disc = getEtat(3, 0);
    int piquet=disques[disc].surLePiquet;
    int h=calculHauteur(piquet);

    setEtat(3, 0, 5);
    setEtat(piquet, h, disc);
    disques[disc].repere.setConstraint(immobile);
  }

  void dragageGuide() {
    int  disc = getEtat(3, 0);
    if (disc<5) {
      int piquet=disques[disc].surLePiquet;
      int h=calculHauteur(piquet);
      disques[disc].repere.setConstraint(contrainteGuide);
      if (disques[disc].repere.position().z()>120) situation=2;
      else if (disques[disc].repere.position().z()<12*h)
      {   
        if (!regle(disc, piquet, h))ejection(disc, piquet);
        else {
          disques[disc].repere.setPosition(new Vec(piquet*150-150, 0, 12*h));
          setEtat(3, 0, 5);
          setEtat(piquet, h, disc);
          disques[disc].repere.setConstraint(immobile);
          situation=0;
          delay(1000);
        }
      }
    }
    else {
      delay(1000);
      situation=0;
    }
  }

  void ejection(int nodisc, int nopiquet) { 
    disques[nodisc].repere.setPosition(150*nopiquet-150, 0, 110);
  }

  boolean regle(int nodisc, int nopiquet, int haut) {
    boolean rep;
    if (haut==0)
      rep=true;
    else
      rep= (getEtat(nopiquet, haut-1)>nodisc);
    return rep;
  }

  void dragageLibre() {
    int  d = getEtat(3, 0);
    disques[d].repere.setConstraint(contraintePlan);
    float  dis=sqrt(sq(disques[d].repere.position().x())+sq(disques[d].repere.position().y()));
    float  disd=sqrt(sq(disques[d].repere.position().x()-150)+sq(disques[d].repere.position().y()));
    float  disg=sqrt(sq(disques[d].repere.position().x()+150)+sq(disques[d].repere.position().y()));
    if (disques[d].repere.position().z()<120 )
    {
      if (dis<25) {
        disques[d].repere.setPosition(new Vec(0, 0, disques[d].repere.position().z()));
        disques[d].surLePiquet=1;
        situation=1;
      }
      else if (disd<25) {
        disques[d].repere.setPosition(new Vec(150, 0, disques[d].repere.position().z()));
        disques[d].surLePiquet=2;
        situation=1;
      }
      else  if (disg<25) {
        disques[d].repere.setPosition(new Vec(-150, 0, disques[d].repere.position().z()));
        disques[d].surLePiquet=0;
        situation=1;
      }
      else {
        Vec v=disques[d].repere.position();
        v.setZ(125);
        disques[d].repere.setPosition(v);
      }
    }
  }

  void pilote() {
    switch(situation) {
    case 0:
      attenteDeClic();
      break;
    case 1:
      dragageGuide();
      break;
    case 2:
      dragageLibre();
      break;
    case 3:
      atterrissage();
    }
  }

  void imprimeEtat() {
    String s="piquet 0 ---> "+getEtat(0, 0)+"   "+getEtat(0, 1)+"   "+getEtat(0, 2)+"   "+getEtat(0, 3)+"   "+getEtat(0, 4) ;
    println(s);
    s="piquet 1 ---> "+getEtat(1, 0)+"   "+getEtat(1, 1)+"   "+getEtat(1, 2)+"   "+getEtat(1, 3)+"   "+getEtat(1, 4) ;
    println(s);
    s="piquet 2 ---> "+getEtat(2, 0)+"   "+getEtat(2, 1)+"   "+getEtat(2, 2)+"   "+getEtat(2, 3)+"   "+getEtat(2, 4) ;

    println(s);
    s="piquet 3 ---> "+getEtat(3, 0)+"   "+getEtat(3, 1)+"   "+getEtat(3, 2)+"   "+getEtat(3, 3)+"   "+getEtat(3, 4) ;  
    println(s);
    println();
  }
}
