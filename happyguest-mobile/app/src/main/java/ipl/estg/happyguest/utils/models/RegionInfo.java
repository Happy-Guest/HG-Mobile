package ipl.estg.happyguest.utils.models;

public class RegionInfo {

        private final String name;

        private final String description;

        private final String descriptionEN;

        private final String distance;

        private final String link;

        public RegionInfo(String name, String description, String descriptionEN, String distance, String link) {
            this.name = name;
            this.description = description;
            this.descriptionEN = descriptionEN;
            this.distance = distance;
            this.link = link;
        }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public String getDistance() {
        return distance;
    }

    public String getLink() {
        return link;
    }
}
