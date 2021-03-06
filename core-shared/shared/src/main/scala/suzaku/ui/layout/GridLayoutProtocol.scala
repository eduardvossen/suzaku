package suzaku.ui.layout

import arteria.core.{Message, Protocol}
import suzaku.ui.UIProtocol.UIChannel
import suzaku.ui._
import suzaku.ui.style.LengthDimension

import scala.language.implicitConversions

object GridLayoutProtocol extends WidgetProtocol {
  import boopickle.Default._
  import LengthDimension._

  implicit val layoutIdPickler = LayoutId.LayoutIdPickler

  sealed trait TrackDef

  case class TrackSize(size: LengthDimension)                        extends TrackDef
  case class TrackMinMax(min: LengthDimension, max: LengthDimension) extends TrackDef

  case class TrackTemplate(tracks: List[TrackDef]) {
    def ~(size: LengthDimension): TrackTemplate =
      copy(tracks = tracks :+ TrackSize(size))

    def ~(minmax: (LengthDimension, LengthDimension)): TrackTemplate =
      copy(tracks = tracks :+ TrackMinMax(minmax._1, minmax._2))
  }

  object TrackTemplate {
    def apply(tracks: TrackDef*): TrackTemplate = TrackTemplate(tracks.toList)
  }

  case class GridDef(cols: TrackTemplate, rows: TrackTemplate, slots: Seq[Seq[LayoutId]])

  sealed trait LayoutMessage extends Message

  case class SetGrid(grid: GridDef) extends LayoutMessage

  private val mPickler = compositePickler[LayoutMessage]
    .addConcreteType[SetGrid]

  implicit val (messagePickler, witnessMsg1, witnessMsg2) = defineProtocol(mPickler, WidgetExtProtocol.wmPickler)

  final case class ChannelContext(grid: GridDef)

  override val contextPickler = implicitly[Pickler[ChannelContext]]
}
