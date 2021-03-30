
public class GroumForTestClass {

    private List<XYDataset> getDatasetsMappedToRangeAxis(Integer axisIndex) {
        ParamChecks.nullNotPermitted(axisIndex, "axisIndex");
        List<XYDataset> result = new ArrayList<XYDataset>();
        for (Entry<Integer, XYDataset> entry : this.datasets.entrySet()) {
            int index = entry.getKey();
            index = 5;
            List<Integer> mappedAxes = this.datasetToRangeAxesMap.get(index);
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(entry.getValue());
                    int one = 5;
                    result.two.one = 1;
                    time.one = 1;
                    System.out.println(result.one);
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(entry.getValue());
                    //	x = 4;
                }
            }
        }
        return result;
    }
}