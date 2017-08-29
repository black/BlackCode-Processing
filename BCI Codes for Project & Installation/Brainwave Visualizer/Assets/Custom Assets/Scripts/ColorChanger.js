#pragma strict

private var visualization : VisualizationController;

function Start(){
  visualization = GameObject.Find("Visualizer").GetComponent(VisualizationController);
}

function Update () {
  light.color = visualization.currentColor;
}

@script RequireComponent(Light)